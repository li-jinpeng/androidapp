from python

ENV PYTHONUNBUFFERED 1

RUN mkdir /code
WORKDIR /code

RUN pip install pip -U -i https://mirrors.aliyun.com/pypi/simple/
ADD requirements.txt /code/
RUN pip install -r requirements.txt -i https://mirrors.aliyun.com/pypi/simple/
ADD . /code/
