FROM python:3.8

WORKDIR /app/src/

COPY requirements.txt /app/src/requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Conda installation
RUN wget https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh -O miniconda.sh && \
    chmod +x miniconda.sh && \
    ./miniconda.sh -b -p /opt/conda && \
    rm miniconda.sh
	
ENV PATH="/opt/conda/bin:${PATH}"	
	
COPY Mozart/requirements.yml /app/src/requirements.yml
RUN conda env create -f /app/src/requirements.yml

COPY . /app/src/

CMD ["python", "recognition_service.py"]