from django.db import models
import uuid

# Create your models here.
class Post(models.Model):
    id = models.TextField(primary_key=True, default=uuid.uuid4, editable=False)
    title = models.TextField()
    content = models.TextField()
    image = models.TextField()
    time = models.DateTimeField(auto_now_add=True)
    type = models.TextField()
    author = models.TextField()
    money = models.TextField()
    thumbs = models.IntegerField(default=0)
    stores = models.IntegerField(default=0)
    reports = models.IntegerField(default=0)
    icons = models.IntegerField()
    tag = models.TextField()
    hide = models.IntegerField(default=0)
    done = models.IntegerField(default=0)

class User(models.Model):
    id = models.TextField(primary_key=True, default=uuid.uuid4, editable=False)
    nickname = models.TextField()
    profile = models.TextField()
    background = models.TextField()
    studentID = models.TextField()
    department = models.TextField()
    visit = models.IntegerField()
    score = models.IntegerField()
    identified = models.IntegerField()
    name = models.TextField()
    gender = models.TextField()
    wechat_id = models.TextField()
    email = models.TextField()
    phone = models.TextField()
    address = models.TextField()
    birth = models.DateField()
    grade = models.TextField()
    thu = models.IntegerField()
    task = models.IntegerField(default=0)#每日任务，最低位表示是否点赞、次低位表示是否评分、第三位表示是否发表评论。

class Reply(models.Model):
    id = models.TextField(primary_key=True, default=uuid.uuid4, editable=False)
    content = models.TextField()
    image = models.TextField()
    reply_id = models.TextField()
    thumbs = models.IntegerField(default=0)
    reports = models.IntegerField(default=0)
    hide = models.IntegerField(default=0)
    author = models.TextField()
    time = models.DateTimeField(auto_now_add=True)

class Message(models.Model):
    id = models.TextField(primary_key=True, default=uuid.uuid4, editable=False)
    send_id = models.TextField()
    receive_id = models.TextField()
    content = models.TextField()
    time = models.DateTimeField(auto_now_add=True)
    looked = models.IntegerField()
    sender_del = models.IntegerField(default=0)
    receiver_del = models.IntegerField(default=0)

class Food(models.Model):
    id = models.TextField(primary_key=True, default=uuid.uuid4, editable=False)
    name = models.TextField()
    image = models.TextField()
    price = models.DecimalField(max_digits=7, decimal_places=2)
    taste_score = models.IntegerField()
    taste_number = models.IntegerField()
    performance_score = models.IntegerField()
    performance_number = models.IntegerField()
    likes = models.IntegerField()
    stores = models.IntegerField()
    reports = models.IntegerField(default=0)
    hide = models.IntegerField(default=0)
    author = models.TextField()
    tag = models.TextField()
    content = models.TextField()
    time = models.DateTimeField(auto_now_add=True)

class Operator(models.Model):
    id = models.TextField(primary_key=True, default=uuid.uuid4, editable=False)
    type = models.IntegerField()#暂定1点赞、2收藏、3评分、4举报、5拉黑
    user_id = models.TextField()
    reply_id = models.TextField()
    first_score = models.IntegerField(default=0)
    second_score = models.IntegerField(default=0)
    time = models.DateTimeField(auto_now_add=True)

class Class(models.Model):
    id = models.TextField(primary_key=True, default=uuid.uuid4, editable=False)
    name = models.TextField()
    teacher = models.TextField()
    recommand_score = models.IntegerField()
    recommand_number = models.IntegerField()
    hard_score = models.IntegerField()
    hard_number = models.IntegerField()
    likes = models.IntegerField()
    stores = models.IntegerField()
    reports = models.IntegerField(default=0)
    author = models.TextField()
    hide = models.IntegerField(default=0)
    content = models.TextField()
    time = models.DateTimeField(auto_now_add=True)

class Draft(models.Model):
    id = models.TextField(primary_key=True, default=uuid.uuid4, editable=False)
    title = models.TextField()
    content = models.TextField()
    image = models.TextField()
    time = models.DateTimeField(auto_now_add=True)
    type = models.IntegerField()
    author = models.TextField()