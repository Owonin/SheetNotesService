from kafka import KafkaConsumer, KafkaProducer
from PIL import Image
import base64
import subprocess
import os
import re
from mido import MidiFile, MidiTrack, Message


ticks_per_beat = 960

# Настройки для Kafka
kafka_broker = 'localhost:9092'
kafka_topic = 'GROUP.PICTURE'
kafka_output_topic = 'GROUP.MIDI'

input_path = 'input/'
output_path = 'output/'

# Функция для сохранения изображения
def save_image_from_bytes(key, value):
    try:
        img_file = open(f'input/{key}.jpg', 'wb')
        img_file.write(value)
        img_file.close()
    except Exception as e:
        print(f"Ошибка при сохранении изображения: {e}")

# Функция для запуска сервиса распознования
def start_recognotion():
    try:
        command = f'python Mozart/src/main.py {input_path} {output_path}'

        conda_env = 'mozart'

        activate_conda = f'conda activate {conda_env} && {command}'

        # Данная фукнция активирует среду конды и запускает распознования изображения 
        result = subprocess.run(activate_conda, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True)

    except Exception as e:
        print(f"Ошибка при запуске сервиса распознования: {e}")
    print("Proccess proceed")

# Функция для отправки данных по Kafka
def send_data_to_kafka(key, value):
    producer = KafkaProducer(bootstrap_servers=kafka_broker)
    producer.send(topic=kafka_output_topic,key=key,value=value)
    producer.close()

# Функция для парсинга нот
def parse_note(note_str):
    note_names = {
        'c': 0, 'c#': 1, 'd': 2, 'd#': 3, 'e': 4, 'f': 5, 'f#': 6, 'g': 7, 'g#': 8, 'a': 9, 'a#': 10, 'b': 11
    }
    
    note_and_duration, duration = note_str.split('/')
    note_name, octave = re.split(r'(?=\d+)', note_and_duration, 1)
    note_number = note_names[note_name.lower()] + (int(octave)+5 * 11)+2
    duration = int(duration)
    
    return note_number, duration

# Фунция создания midi трека
def create_midi_track(notes_data, output_filename):
    midi = MidiFile()

    for note_data in notes_data:
        track = MidiTrack()
        midi.tracks.append(track)
        
        active_notes = set()

        for note_group in note_data:
            if "]" in note_group:
                note_group= note_group[:-1]
            if "{" in note_group:
                chord_notes = note_group.strip('{}').split(",")
                for chord_note in chord_notes:
                    note, duration = parse_note(chord_note)
                    note_on = Message('note_on', note=note, velocity=64)
                    track.append(note_on)
                    active_notes.add(note)  
                for note in active_notes:
                    note_off = Message('note_off', note=note, velocity=64, time=int(ticks_per_beat/duration))
                    track.append(note_off)
                active_notes.clear() 

            else:
                note, duration = parse_note(note_group)
                note_on = Message('note_on', note=note, velocity=64, time=int(ticks_per_beat/duration))
                note_off = Message('note_off', note=note, velocity=64, time=int(ticks_per_beat/duration))
                track.append(note_on)
                track.append(note_off)
           
    midi.save(output_filename)

def main():
   
    consumer = KafkaConsumer(kafka_topic, bootstrap_servers=kafka_broker, group_id='image_processor')
    for message in consumer:
        key = message.key
        value = message.value
        decodedValue = base64.decodebytes(value)

        save_image_from_bytes(key, decodedValue)

        start_recognotion()

        with open(f'output/{key}.txt', 'r') as file:
            text_data = file.read()

        print(f'Данные распознования {text_data}')

        # Извлечение строк нот
        sheet_lines = text_data.strip('{}').split(', ')

        # Удаление подстроки и квадратных скобок из элементов и преобразование в массив
        notes_array = [line.strip('[]').replace('\\meter<\"4/4\">', '') 
                        for line in sheet_lines]
        
        # Разделение нот 
        notes = [note.strip().split(' ') for note in notes_array]
        
        create_midi_track(notes, "output/output.mid")  

        with open(f'output/output.mid', 'rb') as file:
            midi_data = file.read()

        send_data_to_kafka(key, base64.b64encode(midi_data))

        os.remove(f'output/{key}.txt')
        os.remove(f'output/output.mid')
        os.remove(f'input/{key}.png')

if __name__ == '__main__':
    main()
