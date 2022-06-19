from django.urls import reverse
from django.shortcuts import render, get_object_or_404, redirect
from django.http import *
from django.utils import timezone
from django.shortcuts import render
from django.core import serializers
from .models import *
import uuid
import random
import threading
import datetime
import time
import os
import base64
import requests,json
import ahocorasick

img_server = './img/'
filtertree = []

myserver = "http://101.43.128.148:9999"

def random_str(randomlenth=5):
    random_string = ''
    base_str = 'QWERTYUIOPLKJHGFDSAZXCVBNMqwertyuioplkjhgfdsazxvcvbnm0123456789_'
    length = len(base_str) - 1
    for i in range(randomlenth):
        random_string += base_str[random.randint(0,length)]
    return random_string

def randomcolor():
    colorArr = ['1','2','3','4','5','6','7','8','9','A','B','C','D','E','F']
    color = ""
    for i in range(6):
        color += colorArr[random.randint(0,14)]
    return "#"+color

#-------------------------------------------分割线为以下用户相关

def get_user_ava(id): #根据id获取用户头像
    user = User.objects.get(id=id)
    return user.profile

def get_user_nickname(id): #根据id获取用户昵称
    user = User.objects.get(id=id)
    return user.nickname

def translate(number): #转化数字表示
    if number >= 10000:
        t1 = number // 10000
        t2 = (number - 10000*t1) // 1000
        res = str(t1) + "." + str(t2) + "W"
    elif number >= 1000:
        t1 = number // 1000
        t2 = (number - 1000*t1) // 100
        res = str(t1) + "." + str(t2) + "k"
    else:
        res = str(number)
    return res

def get_user_home(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id','')
    black = Operator.objects.filter(user_id = user_id).filter(reply_id = id).filter(type = 5).count()+Operator.objects.filter(user_id = id).filter(reply_id = user_id).filter(type = 5).count() 
    user = User.objects.get(id=id)
    res = {}
    res["account"] = user.email
    res["name"] = user.nickname
    res["ava"] = user.profile
    res["intro"]=user.wechat_id
    res1 = {}
    res1['name']="已被拉黑"
    follows = Operator.objects.filter(type=6).filter(user_id=id)
    count = 0
    for follow in follows:
        count+=1
    res["follow"] = str(count)
    
    followeds = Operator.objects.filter(type=6).filter(reply_id=id)
    count = 0
    for follow in followeds:
        count+=1
    res["followed"] = str(count)
    if black!=0:
        return JsonResponse(res1,safe=False)
    else:
        return JsonResponse(res,safe=False)
    
def get_user_detail(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id','')
    user = User.objects.get(id=id)
    res1 = {}
    res1['name']="已被拉黑"
    black = Operator.objects.filter(user_id = user_id).filter(reply_id = id).filter(type = 5).count()+Operator.objects.filter(user_id = id).filter(reply_id = user_id).filter(type = 5).count() 
    res = {}
    res["name"] = user.nickname
    res["ava"] = user.profile
    res["intro"] = user.wechat_id
    res["account"] = user.email
    res["user_id"] = user.id 
    follow = Operator.objects.filter(user_id = user_id).filter(reply_id = id).filter(type = 6)
    if follow:
        res["follow"] = "已关注"
    else:
        res["follow"] = "未关注"
    if black!=0:
        print(black)  
        print(res1) 
        res1['follow']="未知" 
        return JsonResponse(res1,safe=False)
    else:
        return JsonResponse(res,safe=False)
    
def change_avatar(request):
    myfile = request.FILES.get('image','')
    user_id = request.POST.get('user_id','')
    user = User.objects.get(id=user_id)
    if myfile:
        dir = "pic/" + myfile.name
        w = open(dir,'wb+')
        for chunk in myfile.chunks():   
            w.write(chunk)
        w.close()
        user.profile = (myserver + "/" + dir)
        user.save()
        return JsonResponse({'message':"success"})
    else:
        return JsonResponse({'message':"fail"})

def login(request):
    password = request.POST.get('password', '')
    accounts = User.objects.filter(email = request.POST['account'])
    
    res = {}
    for account in accounts:
      if account:
          print(account.grade)
          if account.grade == password:
              res['message'] = account.id
              res['type'] = 'ok'
          else:
              res['type'] = 'nok'
              res['message'] = "密码错误"
          print(res)
          return JsonResponse(res,safe=False)
    res['type'] = 'nok'
    res['message'] = "账号不存在"
    return JsonResponse(res,safe=False)
    
def change_password(request):
    id = request.POST.get('id', '')
    password = request.POST.get('password', '')
    user = User.objects.get(id=id)
    user.grade = password
    user.save()
    res = {}
    res['message'] = 'ok'
    return JsonResponse(res,safe=False)
    



def new_user(request):
    nickname = request.POST.get('name', '')
    account = request.POST.get('account','')
    password = request.POST.get('password','')
    intro = request.POST.get('intro','')
    user = User()
    user.nickname = nickname
    user.email = account
    user.school = intro
    user.grade = password
    user.save()
    message = Message()
    message.send_id = "notice_follow"
    message.receive_id = user.id
    message.content = "欢迎!"
    message.save()
    message = Message()
    message.send_id = "notice_like"
    message.receive_id = user.id
    message.content = "欢迎!"
    message.save()
    message = Message()
    message.send_id = "notice_upgrade"
    message.receive_id = user.id
    message.content = "欢迎!"
    message.save()
    message = Message()
    message.send_id = "notice_comment"
    message.receive_id = user.id
    message.content = "欢迎!"
    message.save()
    return JsonResponse({'type': 1})

def edit_user(request):
    id = request.POST.get('id', '')
    wechat_id = request.POST.get('intro', '')
    nickname = request.POST.get('name','')
    user = User.objects.get(id=id)
    if nickname != '':
        user.nickname = nickname
    if wechat_id !='':
        user.wechat_id = wechat_id
    user.save()
    return JsonResponse("success!", safe=False)

def user_task(request):
    id = request.POST.get('id', '')
    user = User.objects.get(id=id)
    task = user.task
    res = {}
    res["task1"] = task & 1
    res["task2"] = int((task & 2) / 2)
    res["task3"] = int((task & 4) / 4)
    return JsonResponse({'data': res})

def user_store(request):
    id = request.POST.get('id', '')
    stores = Operator.objects.filter(type=2).filter(user_id=id).order_by('-time')
    res = []
    for store in stores:
        item = {}
        id = store.reply_id
        item["id"] = id
        item["hide"] = 1
        if Food.objects.filter(id=id).count() == 1:
            food = Food.objects.get(id=id)
            item["color"] = "green"
            item["route"] = "/pages/index/food/detail/detail?food_id=" + id
            item["text"] = food.name
            item["time"] = store.time
            item["typename"] = "美食评价"
            item["hide"] = food.hide
        elif Class.objects.filter(id=id).count() == 1:
            class_ = Class.objects.get(id=id)
            item["color"] = "cyan"
            item["route"] = "/pages/index/course/detail/detail?course_id=" + id
            item["text"] = class_.name
            item["time"] = store.time
            item["typename"] = "评课"
            item["hide"] = class_.hide
        elif Post.objects.filter(id=id).count() == 1:
            post = Post.objects.get(id=id)
            item["route"] = "/pages/index/detail/detail?post_id=" + id
            item["text"] = post.content
            item["time"] = store.time
            if post.tag == "求助":
                item["color"] = "red"
                item["typename"] = "求助"
            elif post.tag == "代取物品":
                item["color"] = "grey"
                item["typename"] = "代取物品"
            elif post.tag == "闲置物品":
                item["color"] = "purple"
                item["typename"] = "闲置物品"
            elif post.tag == "倾诉":
                item["color"] = "pink"
                item["typename"] = "倾诉"
            elif post.tag == "寻物":
                item["color"] = "blue"
                item["typename"] = "寻物"
            item["hide"] = post.hide
        if item["hide"] == 0:
            res.append(item)
    return JsonResponse({'data': res})


def user_record(request):
    id = request.POST.get('id', '')
    date_list = []
    res = []
    food_sends = Food.objects.filter(author=id).filter(hide=0)
    for food in food_sends:
        date = food.time.date()
        item = {}
        item["color"] = "green"
        item["route"] = "/pages/index/food/detail/detail?food_id=" + food.id
        item["text"] = food.name
        item["time"] = food.time.time()
        item["typename"] = "美食评价"
        if date in date_list:
            for data in res:
                if data["date"] == date:
                    data["text"].append(item)
        else:
            date_list.append(date)
            temp = {}
            temp["date"] = date
            temp["text"] = []
            temp["text"].append(item)
            res.append(temp)
    post_sends = Post.objects.filter(author=id).filter(hide=0)
    for post in post_sends:
        date = post.time.date()
        item = {}
        item["route"] = "/pages/index/detail/detail?post_id=" + post.id
        item["text"] = post.content
        item["time"] = post.time.time()
        if post.tag == "求助":
            item["color"] = "red"
            item["typename"] = "求助"
        elif post.tag == "代取物品":
            item["color"] = "grey"
            item["typename"] = "代取物品"
        elif post.tag == "闲置物品":
            item["color"] = "purple"
            item["typename"] = "闲置物品"
        elif post.tag == "倾诉":
            item["color"] = "pink"
            item["typename"] = "倾诉"
        elif post.tag == "寻物":
            item["color"] = "blue"
            item["typename"] = "寻物"
        if date in date_list:
            for data in res:
                if data["date"] == date:
                    data["text"].append(item)
        else:
            date_list.append(date)
            temp = {}
            temp["date"] = date
            temp["text"] = []
            temp["text"].append(item)
            res.append(temp)
        course_sends = Class.objects.filter(author=id).filter(hide=0)
        for class_ in course_sends:
            date = class_.time.date()
            item = {}
            item["color"] = "cyan"
            item["route"] = "/pages/index/course/detail/detail?course_id=" + class_.id
            item["text"] = class_.name
            item["time"] = class_.time.time()
            item["typename"] = "评课"
            if date in date_list:
                for data in res:
                    if data["date"] == date:
                        data["text"].append(item)
            else:
                date_list.append(date)
                temp = {}
                temp["date"] = date
                temp["text"] = []
                temp["text"].append(item)
                res.append(temp)
    scores = Operator.objects.filter(type=3).filter(user_id=id)
    for score in scores:
        id = score.reply_id
        date = score.time.date()
        item = {}
        if Food.objects.filter(id=id).count() == 1:
            food = Food.objects.get(id=id)
            item["color"] = "green"
            item["route"] = "/pages/index/food/detail/detail?food_id=" + id
            item["text"] = food.name
            item["time"] = score.time.time()
            item["typename"] = "美食评价"
            if food.hide == 0:
                continue
        elif Class.objects.filter(id=id).count() == 1:
            class_ = Class.objects.get(id=id)
            item["color"] = "cyan"
            item["route"] = "/pages/index/course/detail/detail?course_id=" + id
            item["text"] = class_.name
            item["time"] = score.time.time()
            item["typename"] = "评课"
            if class_.hide == 0:
                continue
        elif Post.objects.filter(id=id).count() == 1:
            post = Post.objects.get(id=id)
            item["route"] = "/pages/index/detail/detail?post_id=" + id
            item["text"] = post.content
            item["time"] = score.time.time()
            if post.tag == "求助":
                item["color"] = "red"
                item["typename"] = "求助"
            elif post.tag == "代取物品":
                item["color"] = "grey"
                item["typename"] = "代取物品"
            elif post.tag == "闲置物品":
                item["color"] = "purple"
                item["typename"] = "闲置物品"
            elif post.tag == "倾诉":
                item["color"] = "pink"
                item["typename"] = "倾诉"
            elif post.tag == "寻物":
                item["color"] = "blue"
                item["typename"] = "寻物"
            if post.hide == 0:
                continue
        if date in date_list:
            for data in res:
                if data["date"] == date:
                    data["text"].append(item)
        else:
            date_list.append(date)
            temp = {}
            temp["date"] = date
            temp["text"] = []
            temp["text"].append(item)
            res.append(temp)
    for item in res: #去重
        temp = []
        for i in item["text"]:
            if i in temp:
                continue
            else:
                temp.append(i)
        item["text"] = temp
    res = sorted(res, key=lambda keys: keys["date"])
    res.reverse()
    for item in res:
        item["text"] = sorted(item["text"], key=lambda keys: keys["time"])
    return JsonResponse({'data': res})

def user_blacklist(request):
    user_id = request.POST.get('user_id', '')
    black_id = request.POST.get('black_id', '')
    print(black_id)
    flag = Operator.objects.filter(type=5).filter(user_id=user_id).filter(reply_id=black_id).count()
    if flag == 0:  # 操作记录不存在
        op = Operator()
        op.reply_id = black_id
        op.user_id = user_id
        op.type = 5
        op.save()
    else:
        op = Operator.objects.filter(type=5).filter(user_id=user_id).filter(reply_id=black_id)
        op.delete()
    return JsonResponse("success!", safe=False)
    
def user_follow(request):
    user_id = request.POST.get('user_id', '')
    black_id = request.POST.get('follow_id', '')
    print(black_id)
    flag = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=black_id).count()
    if flag == 0:  # 操作记录不存在
        op = Operator()
        op.reply_id = black_id
        op.user_id = user_id
        op.type = 6
        op.save()
        message = Message()
        message.send_id = "notice_follow"
        message.receive_id = black_id
        message.content = get_user_nickname(user_id)+"关注了您！"
        message.looked = 0
        message.save()
        
    else:
        op = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=black_id)
        op.delete()
    return JsonResponse("success!", safe=False)
    
def user_follows(request):
    user_id = request.POST.get('id','')
    res = []
    ops = Operator.objects.filter(type=6).filter(user_id = user_id)
    for op in ops:
        id_ = op.reply_id
        print(id_)
        user = User.objects.get(id = id_)
        item ={}
        item["ava"] = user.profile
        item["user_id"] = user.id
        item["name"] = user.nickname
        follow = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=user.id)
        if follow:
            item["follow"] = "已关注"
        else:
            item["follow"] = "未关注"
        res.append(item)
    return JsonResponse(res, safe=False)

def user_followeds(request):
    user_id = request.POST.get('id','')
    res = []
    ops = Operator.objects.filter(type=6).filter(reply_id = user_id)

    for op in ops:
        id_ = op.user_id
    
        user = User.objects.get(id = id_)
        item = {}
        item["ava"] = user.profile
        item["user_id"] = user.id
        item["name"] = user.nickname
        follow = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=user.id)
        if follow:
            item["follow"] = "已关注"
        else:
            item["follow"] = "未关注"
        res.append(item)
    return JsonResponse(res, safe=False)
#-------------------------------------------

def post_reply(request):
    reply = Reply()
    reply.reply_id = request.POST.get('reply_id', '')
    reply.content = check_word(request.POST.get('content', ''))
    reply.image = request.POST.get('image', '')
    reply.thumbs = 0
    author = request.POST.get('author', '')
    reply.author = author
    reply.save()
    user = User.objects.get(id=author)
    if user.task & 4 == 0:
        user.task = user.task | 4
        user.score = user.score + 50
        user.save()
    flag = Food.objects.filter(id=reply.reply_id).count()
    if flag == 1:
        food = Food.objects.get(id=reply.reply_id)
        author = User.objects.get(id=food.author)
    else:
        flag = Post.objects.filter(id=reply.reply_id).count()
        if flag == 1:
            post = Post.objects.get(id=reply.reply_id)
            author = User.objects.get(id=post.author)
        else:
            flag = Class.objects.filter(id=reply.reply_id).count()
            if flag == 1:
                course = Class.objects.get(id=reply.reply_id)
                author = User.objects.get(id=course.author)
            else:
                reply_ = Reply.objects.get(id=reply.reply_id)
                author = User.objects.get(id=reply_.author)
    author.visit = author.visit + 10
    author.save()
    message = Message()
    message.send_id = "notice_comment"
    message.receive_id = author.id
    message.content = "“" + user.nickname + "”" + "评论了您的作品<" + post.title +">"
    message.looked = 0
    message.save()
    return JsonResponse("success!", safe=False)

def delete_reply(request):
    id = request.POST.get('id', '')
    reply = Reply.objects.filter(id=id)
    reply.delete()
    return JsonResponse("success", safe=False)

#-------------------------------------------分割线为以下消息相关

def get_message_index(request):
    id = request.POST.get('id', '')
    res = []
    users = []

    receives = Message.objects.filter(receive_id=id).order_by("-time").filter(receiver_del=0)
    
    for message in receives:
        if message.send_id not in users:
            users.append(message.send_id)
            item = {}
            item["id"] = message.send_id
            item["sender"] = get_user_nickname(message.send_id)
            item["msg"] = message.content 
            item["year"] = message.time.year
            item["mon"] = message.time.month
            item["day"] = message.time.day
            item["hour"] = message.time.hour
            item["min"] = int(message.time.strftime("%M"))
            item['num'] = Message.objects.filter(receive_id=id).filter(send_id=message.send_id).filter(looked=0).count()
            res.append(item)
        else:
            continue
    return JsonResponse(res,safe=False)

def get_message_detail(request):
    user_id = request.POST.get('user_id', '')
    id = request.POST.get('id', '')
    res = []
    msgs = Message.objects.filter(send_id=id).filter(receive_id=user_id).order_by("-time").filter(sender_del=0)
    
    for message in msgs:
        message.looked = 1
        message.save()
        item = {}
        item["sender"] = get_user_nickname(message.send_id)
        item["msg"] = message.content 
        item["year"] = message.time.year
        item["mon"] = message.time.month
        item["day"] = message.time.day
        item["hour"] = message.time.hour
        item["min"] = int(message.time.strftime("%M"))
        res.append(item)    
    return JsonResponse(res,safe=False)

def send_message(request):
    send_id = request.POST.get('send_id', '')
    receive_id = request.POST.get('receive_id', '')
    content = request.POST.get('content', '')
    message = Message()
    message.send_id = send_id
    message.receive_id = receive_id
    flag = Operator.objects.filter(type=5).filter(user_id=receive_id).filter(reply_id=send_id).count() + Operator.objects.filter(type=5).filter(user_id=send_id).filter(reply_id=receive_id).count()
    if flag == 0:
        message.content = content
    else:
        message.content = ""
        message.receiver_del = 1
    message.looked = 0
    message.save()
    return JsonResponse("success!", safe=False)

def delete_message(request):
    user_id = request.POST.get('user_id', '')
    id = request.POST.get('id', '')
    sends = Message.objects.filter(send_id=user_id).filter(receive_id=id).filter(sender_del=0)
    receives = Message.objects.filter(send_id=id).filter(receive_id=user_id).filter(receiver_del=0)
    for send in sends:
        send.sender_del = 1
        send.save()
    for receive in receives:
        receive.receiver_del = 1
        receive.save()
    return JsonResponse("success!", safe=False)

#-------------------------------------------分割线以下为美食相关

def get_food_index(request):#主页获取美食信息
    foods = Food.objects.all().filter(hide=0).order_by("-time")
    res = []
    for food in foods:
        item = {}
        item["id"] = food.id
        item["name"] = food.name
        temp = food.image.split(",")
        if len(temp) == 0:
            item["bgPath"] = ""
        else:
            item["bgPath"] = temp[0]
        item["price"] = food.price
        item["palate"] = round(food.taste_score / food.taste_number, 2)
        item["price_"] = round(food.performance_score / food.performance_number, 2)
        item["tags"] = food.tag
        res.append(item)
    return JsonResponse({'data': res})

def get_food_detail(request):#获取美食详情页数据
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    food = Food.objects.get(id=id)
    res = {}
    res["name"] = food.name
    res["tags"] = food.tag
    res["price"] = food.price
    res["palate"] = round(food.taste_score / food.taste_number, 2)
    res["price_"] = round(food.performance_score / food.performance_number, 2)
    res["pic"] = food.image.split(",")
    res["likes"] = food.likes
    res["store"] = food.stores
    res["reports"] = food.reports
    res["like"] = Operator.objects.filter(type=1).filter(user_id=user_id).filter(reply_id=id).count()
    res["sto"] = Operator.objects.filter(type=2).filter(user_id=user_id).filter(reply_id=id).count()
    res["comment"] = Operator.objects.filter(type=3).filter(user_id=user_id).filter(reply_id=id).count()
    if res["comment"] != 0:
        op = Operator.objects.filter(type=3).filter(user_id=user_id).filter(reply_id=id).first()
        res["taste"] = op.first_score
        res["performance"] = op.second_score
    res["reported"] = Operator.objects.filter(type=4).filter(user_id=user_id).filter(reply_id=id).count()
    response = []
    replys = Reply.objects.filter(reply_id=id).filter(hide=0).order_by("time")
    for reply in replys:
        item = {}
        item["id"] = reply.id
        item["msg"] = reply.content
        item["likes"] = reply.thumbs
        item["reports"] = reply.reports
        item["date"] = reply.time.strftime('%m-%d %H:%M')
        item["user_id"] = reply.author
        item["sendname"] = get_user_nickname(reply.author)
        item["ava"] = get_user_ava(reply.author)
        item["like"] = Operator.objects.filter(type=1).filter(user_id=user_id).filter(reply_id=reply.id).count()
        item["reported"] = Operator.objects.filter(type=4).filter(user_id=user_id).filter(reply_id=reply.id).count()
        response.append(item)
    return JsonResponse({'data': res, "response": response})

def add_food_score(request):#增加美食评分
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    taste = request.POST.get('taste', '')
    performance = request.POST.get('performance', '')
    item = Food.objects.get(id=id)
    if taste != '':
        item.taste_score = item.taste_score + int(taste)
        item.taste_number = item.taste_number + 1
    if performance != '':
        item.performance_score = item.performance_score + int(performance)
        item.performance_number = item.performance_number + 1
    item.save()
    op = Operator()
    op.type = 3
    op.reply_id = id
    op.user_id = user_id
    op.first_score = taste
    op.second_score = performance
    op.save()
    user = User.objects.get(id=user_id)
    if user.task & 2 == 0:
        user.task = user.task | 2
        user.score = user.score + 20
        user.save()
    return JsonResponse("success!", safe=False)

def new_food(request):#新建美食记录
    name = request.POST.get('name', '')
    content = request.POST.get('content', '')
    image = request.POST.get('image', '')
    price = request.POST.get('price', '')
    taste = request.POST.get('taste', '')
    performance = request.POST.get('performance', '')
    tag = request.POST.get('tag', '')
    user_id = request.POST.get('user_id', '')
    

    food = Food()
    food.image = ''

    if image!= '':
        imgList = image.split(',')
        for i in range(len(imgList)):
            image_data = base64.b64decode(imgList[i])
            pic_id = random_str(20)
            with open('pic/'+pic_id+'.png','wb') as f:
                f.write(image_data)
                f.close
            if food.image == '':
                food.image = img_server+pic_id+'.png'
            else:
                food.image += (',' + img_server+pic_id+'.png')
    else:
        food.image = img_server + "default.jpg/"

    food.name = check_word(name)
    food.price = float(price)
    if taste == '':
        food.taste_score = 0
    else:
        food.taste_score = int(taste)
    if performance == '':
        food.performance_score = 0
    else:
        food.performance_score = int(performance)
    food.taste_number = 1
    food.performance_number = 1
    food.likes = 0
    food.stores = 0
    food.content = check_word(content)
    food.tag = tag
    food.author = user_id
    food.save()
    reply_id = food.id
    reply = Reply()
    reply.reply_id = reply_id
    reply.content = check_word(content)
    reply.thumbs = 0
    reply.author = user_id
    reply.save()
    return JsonResponse("success!", safe=False)

#-------------------------------------------分割线以下为评事相关
def get_post_index(request):
    user_id = request.POST.get('user_id','')
    tag = request.POST.get('tag', '')
    if tag == '':
        posts = Post.objects.filter(hide=0).order_by("-time")
    else:
        posts = Post.objects.filter(hide=0).filter(tag=tag).order_by("-time")
    List = []
    i = 0
    for post in posts:
        item = {}
        item["id"] = post.id
        item["time"] = post.time.strftime('%m-%d %H:%M')
        item["content"] = post.content
        if post.image == None or post.image == "":
            item["image"] = False
            item["imagePath"] = ""
        else:
            temp = post.image.split(",")
            item["image"] = True
            item["imagePath"] = temp[0]
        item["title"] = post.title
        item["type"] = post.type
        item["location"] = post.tag
        author = post.author
        user = User.objects.get(id=author)
        item["user_id"] = user.id
        item["avatar"] = user.profile
        item["dep"] = user.department
        if item["dep"] == "" or item["dep"] == None:
            item["dep"] = "未知"
        item["sender"] = user.nickname
        follow = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=user.id)
        if follow:
            item["follow"] = "已关注"
        else:
            item["follow"] = "未关注"
        List.append(item)
    return JsonResponse(List,safe=False)

def get_post_detail(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    post = Post.objects.get(id=id)
    res = {}
    res["author"]=post.author
    res["sendname"] = get_user_nickname(post.author)
    res["date"] = post.time.strftime('%m-%d %H:%M')
    res["ava"] = get_user_ava(post.author)
    res["title"] = post.title
    res["text"] = post.content
    if post.image==None:
        res["pic"] = [];
    else:
        res["pic"] = post.image.split(",")
    res["type"] = post.type
    res["likes"] = post.thumbs
    res["store"] = post.stores
    res["reports"] = post.reports
    res["location"] = post.tag
    res["like"] = Operator.objects.filter(type=1).filter(user_id=user_id).filter(reply_id=id).count()
    res["sto"] = Operator.objects.filter(type=2).filter(user_id=user_id).filter(reply_id=id).count()
    res["reported"] = Operator.objects.filter(type=4).filter(user_id=user_id).filter(reply_id=id).count()
    thumb_list = "点赞者:"
    if res["likes"] == 0:
      thumb_list += "无"
    else:
      peoples = Operator.objects.filter(type=1).filter(reply_id=id)
      for people in peoples:
        user = User.objects.get(id=people.user_id)
        thumb_list += user.nickname + " "
    res["thumb_list"] = thumb_list
    
    #res["done"] = Reply.objects.filter(author=user_id).filter(reply_id=id).count()
    #if res["done"] > 0:
        #res["done"] = 1
    #res["icons"] = post.icons
    #res["money"] = post.money
    #res["finished"] = post.done
    response = []
    users = []
    tmp1 = []
    second_replys = []
    replys = Reply.objects.filter(reply_id=id).filter(hide=0)

    for reply in replys:
        item = {}
        item["id"] = reply.id
        item["rank"] = 1
        item["user_id"] = reply.author
        
        item["sendname"] = get_user_nickname(reply.author)
        item["ava"] = get_user_ava(reply.author)
        item["msg"] = reply.content
        item["likes"] = reply.thumbs
        item["reports"] = reply.reports
        item["date"] = reply.time
        item["like"] = Operator.objects.filter(type=1).filter(user_id=user_id).filter(reply_id=reply.id).count()
        item["reported"] = Operator.objects.filter(type=4).filter(user_id=user_id).filter(reply_id=reply.id).count()
        response.append(item)
        if reply.author not in tmp1 and reply.author != res["author"]:
          users.append(item)
          tmp1.append(reply.author)
        tmp = Reply.objects.filter(reply_id=reply.id).filter(hide=0)
        for i in tmp:
            second_replys.append(i)
    
    while second_replys != []:
        next_replys = []
        for second_reply in second_replys:
            item = {}
            item["id"] = second_reply.id
            item["rank"] = 2
            item["sendname"] = get_user_nickname(second_reply.author)
            item["user_id"] = second_reply.author
            tmp_id = Reply.objects.get(id=second_reply.reply_id)
            item["recname"] = get_user_nickname(tmp_id.author)
            item["ava"] = get_user_ava(second_reply.author)
            item["msg"] = second_reply.content
            item["likes"] = second_reply.thumbs
            item["reports"] = second_reply.reports
            item["date"] = second_reply.time
            item["like"] = Operator.objects.filter(type=1).filter(user_id=user_id).filter(reply_id=second_reply.id).count()
            item["reported"] = Operator.objects.filter(type=4).filter(user_id=user_id).filter(reply_id=second_reply.id).count()
            response.append(item)
            if second_reply.author not in tmp1 and second_reply.author != res["author"]:
              users.append(item)
              tmp1.append(second_reply.author)
            tmp = Reply.objects.filter(reply_id=second_reply.id).filter(hide=0)
            for i in tmp:
                next_replys.append(i)
        second_replys = next_replys
    response = sorted(response, key=lambda keys: keys["date"])
    for item in response:
        item["date"] = item["date"].strftime('%m-%d %H:%M')
    res["response"] = response
    return JsonResponse(res, safe=False)


def new_post(request):#新建帖子记录

    title  = request.POST.get('title','')
    myfile = request.FILES.get('image',)
    content = request.POST.get('content', '')
    user_id = request.POST.get('user_id', '')
    post_id = request.POST.get('post_id','')
    type = request.POST.get('type','')
    location = request.POST.get('location','')
    if post_id == "init":
        post = Post()
        post.content = check_word(content)
        post.author = user_id
        post.title = title
        post.tag = location
        post.type = type
        if myfile:
            dir = "pic/"  + myfile.name
            w = open(dir,'wb+')
            for chunk in myfile.chunks():   
                w.write(chunk)
            w.close()
            post.image = (myserver + "/" + dir + ";")
        post.save()
        operators = Operator.objects.filter(type=6).filter(reply_id=user_id)
        user = User.objects.get(id=user_id)
        for operator in operators:
          message = Message()
          message.send_id = "notice_upgrade"
          message.receive_id = operator.user_id
          message.content = "你关注的“" + user.nickname + "”" + "发表了作品<" + post.title +">"
          message.looked = 0
          message.save()
        return JsonResponse({'id':post.id})
    else:
        post = Post.objects.get(id=post_id)
        if myfile:
            dir = "pic/" + myfile.name
            w = open(dir,'wb+')
            for chunk in myfile.chunks():   
                w.write(chunk)
            w.close()
            post.image += (myserver + "/" + dir + ";")
            post.save()
        return JsonResponse({'id':post.id})
    

def finish_post(request):
    post_id = request.POST.get('post_id', '')
    user_id = request.POST.get('user_id', '')
    post = Post.objects.get(id=post_id)
    if User.objects.filter(id=user_id).count()==0:
        return JsonResponse("Not Exist User!", safe=False)
    user = User.objects.get(id=user_id)
    user.score = user.score + post.icons
    user.save()
    if post.done == 0:
        post.done = 1
        post.save()
    else:
        return JsonResponse("Post was done!", safe=False)
    return JsonResponse("success!", safe=False)
# -------------------------------------------分割线以下为评课相关

def get_course_index(request):  # 主页获取评课信息
    courses = Class.objects.all().order_by("-time").filter(hide=0)
    res = []
    for course in courses:
        item = {}
        item["id"] = course.id
        item["name"] = course.name
        item["teacher"] = course.teacher
        item["score"] = round(course.recommand_score / course.recommand_number, 2)
        item["hard"] = round(course.hard_score / course.hard_number, 2)
        res.append(item)
    return JsonResponse({'data': res})


def get_course_detail(request):  # 获取评课详情页数据
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    course = Class.objects.get(id=id)
    res = {}
    user_list = []
    ava_dic = {}
    unused = []
    for i in range(16):
        unused.append(i+1)
    res["name"] = course.name
    res["teacher"] = course.teacher
    res["score"] = round(course.recommand_score / course.recommand_number, 2)
    res["hard"] = round(course.hard_score / course.hard_number, 2)
    res["likes"] = course.likes
    res["store"] = course.stores
    res["reports"] = course.reports
    res["like"] = Operator.objects.filter(type=1).filter(
        user_id=user_id).filter(reply_id=id).count()
    res["sto"] = Operator.objects.filter(type=2).filter(
        user_id=user_id).filter(reply_id=id).count()
    res["comment"] = Operator.objects.filter(type=3).filter(
        user_id=user_id).filter(reply_id=id).count()
    if res["comment"] != 0:
        op = Operator.objects.filter(type=3).filter(user_id=user_id).filter(reply_id=id).first()
        res["score1"] = op.first_score
        res["hard1"] = op.second_score
    res["reported"] = Operator.objects.filter(type=4).filter(
        user_id=user_id).filter(reply_id=id).count()
    response = []
    replys = Reply.objects.filter(reply_id=id).order_by("time").filter(hide=0)
    for reply in replys:
        item = {}
        item["id"] = reply.id
        item["msg"] = reply.content
        item["likes"] = reply.thumbs
        item["reports"] = reply.reports
        item["date"] = reply.time.strftime('%m-%d %H:%M')
        item["like"] = Operator.objects.filter(type=1).filter(
            user_id=user_id).filter(reply_id=reply.id).count()
        item["reported"] = Operator.objects.filter(type=4).filter(
            user_id=user_id).filter(reply_id=reply.id).count()
        user = reply.author
        if user in user_list:
            item["ava"] = ava_dic[user]
        else:
            user_list.append(user)
            if len(unused)>0:
                tmp = 1
                number = unused[tmp-1]
            else:
                for i in range(16):
                    unused.append(i+1)
                tmp = 1
                number = unused[tmp-1]
            del unused[tmp-1]
            item["ava"] = img_server + str(number) +".jpg"
            ava_dic[user] = item["ava"]
        response.append(item)
    return JsonResponse({'data': res, "response": response})


def add_course_score(request):  # 增加评课评分
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    score = request.POST.get('score', '')
    hard = request.POST.get('hard', '')
    item = Class.objects.get(id=id)
    if score != '':
        item.recommand_score = item.recommand_score + int(score)
        item.recommand_number = item.recommand_number + 1
    if hard != '':
        item.hard_score = item.hard_score + int(hard)
        item.hard_number = item.hard_number + 1
    item.save()
    op = Operator()
    op.type = 3
    op.reply_id = id
    op.user_id = user_id
    op.first_score = score
    op.second_score = hard
    op.save()
    user = User.objects.get(id=user_id)
    if user.task & 2 == 0:
        user.task = user.task | 2
        user.score = user.score + 20
        user.save()
    return JsonResponse("success!", safe=False)


def new_course(request):  # 新建评课记录
    name = request.POST.get('name', '')
    teacher = request.POST.get('teacher', '')
    content = request.POST.get('content', '')
    score = request.POST.get('score', '')
    hard = request.POST.get('hard', '')
    user_id = request.POST.get('user_id', '')
    course = Class()
    course.name = check_word(name)
    if score == '':
        course.recommand_score = 0
    else:
        course.recommand_score = int(score)
    if hard == '':
        course.hard_score = 0
    else:
        course.hard_score = int(hard)
    course.recommand_number = 1
    course.hard_number = 1
    course.likes = 0
    course.stores = 0
    course.teacher = check_word(teacher)
    course.content = check_word(content)
    course.author = user_id
    course.save()
    reply_id = course.id
    reply = Reply()
    reply.reply_id = reply_id
    reply.content = check_word(content)
    reply.thumbs = 0
    reply.author = user_id
    reply.save()
    return JsonResponse("success!", safe=False)

#-------------------------------------------分割线以下为操作相关

def good_post(request):
    pass
    

def edit_operator(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    type = int(request.POST.get('type', ''))
    reply_type = int(request.POST.get('reply_type', ''))
    flag = Operator.objects.filter(type=type).filter(user_id=user_id).filter(reply_id=id).count()
    user = User.objects.get(id=user_id)
    if flag == 0: #操作记录不存在
        op = Operator()
        op.reply_id = id
        op.user_id = user_id
        op.type = type
        op.save()
        if reply_type == 1:
            food = Food.objects.get(id=id)
            if type == 1:
                food.likes = food.likes + 1
                if user.task & 1 == 0:
                    user.task = user.task | 1
                    user.score = user.score + 10
                    user.save()
                author = User.objects.get(id=food.author)
                author.visit = author.visit + 3
                author.save()
            elif type == 2:
                food.stores = food.stores + 1
                author = User.objects.get(id=food.author)
                author.visit = author.visit + 3
                author.save()
            elif type == 4:
                food.reports = food.reports + 1
                if food.reports == 5 and food.hide == 0:
                    food.hide = 1
                    message = Message()
                    message.send_id = "admin"
                    message.receive_id = food.author
                    message.content = "您的帖子 " + food.name + " 被举报过多，已被隐藏！"
                    message.looked = 0
                    message.save()
            food.save()
        elif reply_type == 2:
            post = Post.objects.get(id=id)
            if type == 1:
                post.thumbs = post.thumbs + 1
                message = Message()
                message.send_id = "notice_like"
                message.receive_id = post.author
                message.content = "“" + user.nickname + "”" + "点赞了您的作品<" + post.title +">"
                message.looked = 0
                message.save()
                if user.task & 1 == 0:
                    user.task = user.task | 1
                    user.score = user.score + 10
                    user.save()
                author = User.objects.get(id=post.author)
                author.visit = author.visit + 3
                author.save()
            elif type == 2:
                post.stores = post.stores + 1
                author = User.objects.get(id=post.author)
                author.visit = author.visit + 3
                author.save()
            elif type == 4:
                post.reports = post.reports + 1
                if post.reports == 5 and post.hide == 0:
                    post.hide = 1
                    message = Message()
                    message.send_id = "admin"
                    message.receive_id = post.author
                    message.content = "您的帖子 " + post.content + " 被举报过多，已被隐藏！"
                    message.looked = 0
                    message.save()
            post.save()
        elif reply_type == 3:
            course = Class.objects.get(id=id)
            if type == 1:
                course.likes = course.likes + 1
                if user.task & 1 == 0:
                    user.task = user.task | 1
                    user.score = user.score + 10
                    user.save()
                author = User.objects.get(id=course.author)
                author.visit = author.visit + 3
                author.save()
            elif type == 2:
                course.stores = course.stores + 1
                author = User.objects.get(id=course.author)
                author.visit = author.visit + 3
                author.save()
            elif type == 4:
                course.reports = course.reports + 1
                if course.reports == 5 and course.hide == 0:
                    course.hide = 1
                    message = Message()
                    message.send_id = "admin"
                    message.receive_id = course.author
                    message.content = "您的帖子 " + course.name + " 被举报过多，已被隐藏！"
                    message.looked = 0
                    message.save()
            course.save()
        elif reply_type == 4:
            reply = Reply.objects.get(id=id)
            if type == 1:
                reply.thumbs = reply.thumbs + 1
                if user.task & 1 == 0:
                    user.task = user.task | 1
                    user.score = user.score + 10
                    user.save()
                author = User.objects.get(id=reply.author)
                author.visit = author.visit + 3
                author.save()
            elif type == 4:
                reply.reports = reply.reports + 1
                if reply.reports == 5 and reply.hide == 0:
                    reply.hide = 1
                    message = Message()
                    message.send_id = "admin"
                    message.receive_id = reply.author
                    message.content = "您的评论 " + reply.content + " 被举报过多，已被隐藏！"
                    message.looked = 0
                    message.save()
            reply.save()
    elif flag == 1:
        op = Operator.objects.filter(type=type).filter(user_id=user_id).filter(reply_id=id)
        op.delete()
        if reply_type == 1:
            food = Food.objects.get(id=id)
            if type == 1:
                food.likes = food.likes - 1
                author = User.objects.get(id=food.author)
                author.visit = author.visit - 3
                author.save()
            elif type == 2:
                food.stores = food.stores - 1
                author = User.objects.get(id=food.author)
                author.visit = author.visit - 3
                author.save()
            elif type == 4:
                food.reports = food.reports - 1
            food.save()
        elif reply_type == 2:
            post = Post.objects.get(id=id)
            if type == 1:
                post.thumbs = post.thumbs - 1
                author = User.objects.get(id=post.author)
                author.visit = author.visit - 3
                author.save()
            elif type == 2:
                post.stores = post.stores - 1
                author = User.objects.get(id=post.author)
                author.visit = author.visit - 3
                author.save()
            elif type == 4:
                post.reports = post.reports - 1
            post.save()
        elif reply_type == 3:
            course = Class.objects.get(id=id)
            if type == 1:
                course.likes = course.likes - 1
                author = User.objects.get(id=course.author)
                author.visit = author.visit - 3
                author.save()
            elif type == 2:
                course.stores = course.stores - 1
                author = User.objects.get(id=course.author)
                author.visit = author.visit - 3
                author.save()
            elif type == 4:
                course.reports = course.reports - 1
            course.save()
        elif reply_type == 4:
            reply = Reply.objects.get(id=id)
            if type == 1:
                reply.thumbs = reply.thumbs - 1
                author = User.objects.get(id=reply.author)
                author.visit = author.visit - 3
                author.save()
            elif type == 4:
                reply.reports = reply.reports - 1
            reply.save()
    return JsonResponse("success!", safe=False)

def index_search(request):
    user_type = request.POST.get('user_type','')
    search = request.POST.get('search', '')
    type = request.POST.get('type', '')
    order = request.POST.get('order', '')
    user_id = request.POST.get('user_id', '')
    attention = request.POST.get('attention', '')
    mine = request.POST.get('')
    search_tag = search.split(",")
    res = []
    if order == "1":
        posts = Post.objects.order_by("-thumbs")
    else:
        posts = Post.objects.order_by("-time")
    if type == "内容":
        for tag in search_tag:
            posts = posts.filter(content__icontains=tag)
    elif type == "标题":
        for tag in search_tag:
            posts = posts.filter(title__icontains=tag)
    elif type == "类型":
        for tag in search_tag:
            posts = posts.filter(type__icontains=tag)
    for post in posts:
        item = {}
        item["id"] = post.id
        item["time"] = post.time.strftime('%m-%d %H:%M')
        item["content"] = post.content
        item["title"] = post.title
        if post.image == None or post.image == "":
            item["image"] = False
            item["imagePath"] = ""
        else:
            temp = post.image.split(";")
            item["image"] = True
            item["imagePath"] = temp[0]
        item["type"] = post.type
        item["location"] = post.tag
        item["thumbs"] = post.thumbs
        author = post.author
        user = User.objects.get(id=author)
        item["avatar"] = user.profile
        item["dep"] = user.department
        item["user_id"] = user.id
        if item["dep"] == "" or item["dep"] == None:
            item["dep"] = "未知"
        item["sender"] = user.nickname
        follow = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=user.id)
        if follow:
            item["follow"] = "已关注"
        else:
            item["follow"] = "未关注"
        thumb = Operator.objects.filter(type=1).filter(user_id=user_id).filter(reply_id=post.id).count()
        item["thumb"] = thumb
        flag = 1
        if mine == "1":
            if item["user_id"]!=user_id:
                flag = 0
        if attention == "1":
            flag = Operator.objects.filter(type=6).filter(user_id=user_id).filter(reply_id=item["user_id"]).count()
        if type == "用户":
            for tag in search_tag:
                if tag not in item["sender"]:
                    flag = 0
        if flag != 0:
            if user_type != 'all':
                black = Operator.objects.filter(user_id = user_id).filter(reply_id = user_type).filter(type = 5).count()+Operator.objects.filter(user_id = user_type).filter(reply_id = user_id).filter(type = 5).count()
                print(black)
                if black==0 and item['user_id']==user_type:
                    res.append(item)
            else:
                res.append(item)
    return JsonResponse(res, safe=False)

def new_draft(request):#新建草稿
    title = request.POST.get('title', '')
    content = request.POST.get('content', '')
    user_id = request.POST.get('user_id', '')
    image = request.POST.get('image','')
    draft = Draft()
    draft.image = ''
    if image!= '':
        imgList = image.split(',')
        for i in range(len(imgList)):
            image_data = base64.b64decode(imgList[i])
            pic_id = random_str(20)
            with open('pic/'+pic_id+'.png','wb') as f:
                f.write(image_data)
                f.close
            if draft.image == '':
                draft.image = img_server+pic_id+'.png'
            else:
                draft.image += (',' + img_server+pic_id+'.png')
    draft.title = check_word(title)
    draft.content = check_word(content)
    draft.author = user_id
    draft.save()
    res = []
    drafts = Draft.objects.filter(author=user_id).order_by("-time")
    for draft in drafts:
        item = {}
        item["id"] = draft.id
        item["titie"] = draft.title
        item["content"] = draft.content
        item["time"] = draft.time.strftime('%m-%d %H:%M')
        res.append(item)
    return JsonResponse(res, safe=False)

def get_draft(request):
    user_id = request.POST.get('user_id', '')
    res = []
    drafts = Draft.objects.filter(author=user_id).order_by("-time")
    for draft in drafts:
        item = {}
        item["id"] = draft.id
        item["titie"] = draft.title
        item["content"] = draft.content
        item["time"] = draft.time.strftime('%m-%d %H:%M')
        res.append(item)
    return JsonResponse(res, safe=False)

def edit_draft(request):
    id = request.POST.get('id', '')
    title = request.POST.get('title', '')
    content = request.POST.get('content', '')
    user_id = request.POST.get('user_id', '')
    drafts = Draft.objects.filter(id=id)
    for draft in drafts:
        draft.title = title
        draft.content = content
        draft.save()
    res = []
    drafts = Draft.objects.filter(author=user_id).order_by("-time")
    for draft in drafts:
        item = {}
        item["id"] = draft.id
        item["titie"] = draft.title
        item["content"] = draft.content
        item["time"] = draft.time.strftime('%m-%d %H:%M')
        res.append(item)
    return JsonResponse(res, safe=False)


def delete_draft(request):
    id = request.POST.get('id', '')
    user_id = request.POST.get('user_id', '')
    draft = Draft.objects.filter(id=id)
    draft.delete()
    res = []
    drafts = Draft.objects.filter(author=user_id).order_by("-time")
    for draft in drafts:
        item = {}
        item["id"] = draft.id
        item["titie"] = draft.title
        item["content"] = draft.content
        item["time"] = draft.time.strftime('%m-%d %H:%M')
        res.append(item)
    return JsonResponse(res, safe=False)

#--------------------------图片----------------------------

def get_image(request,id):
    imagepath = "pic/" + id
    image_data = open(imagepath,"rb").read()
    if ".mp4" in imagepath:
        return HttpResponse(image_data, content_type="video/mp4")
    if ".png" in imagepath or ".jpg" in imagepath or ".jpeg" in imagepath:
        return HttpResponse(image_data, content_type="image/png")
    if ".mp3" in imagepath:
        return HttpResponse(image_data, content_type="video/mp4")
    return JsonResponse("Fail!")

#-------------------------getid----------------------------
def get_id(request):
    code = request.POST.get('code', '')
    r = requests.get('https://api.weixin.qq.com/sns/jscode2session?appid=wxb96edada600b5a49&secret=14935ef4a081f728e26abac9c3054192&js_code='+code+'&grant_type=authorization_code')
    data = json.loads(r.text)
    return JsonResponse(data)

#---------------------------判断是否在认证---------------
def judge_identy(request):
    id = request.POST.get('id', '')
    user = User.objects.get(id=id)
    identy = user.thu
    return JsonResponse({'identy': identy})
#------------------------改变认证状态-----------------
def change_identy(request):
    id = request.POST.get('id', '')
    user = User.objects.get(id=id)
    if user.thu == 0:
        user.thu = 1
    else:
        user.thu = 0
    user.save()
    return JsonResponse({'state': user.thu})
#----------------------获取文档----------------------
def get_doc(request):
    file_name = "helper.pdf"
    file_path = "doc/" + file_name
    with open(file_path, 'rb') as f:
        response = HttpResponse(f.read(), content_type="application/pdf", charset="utf-8")
        response['Content-Dispositon'] = "attachment; filename={0}".format(file_name)
        response["Access-Control-Allow-Origin"] = '*'
        response["Server"] = '*'
        return response

#----------------------敏感词过滤----------------------
def build_actree(wordlist):
    actree = ahocorasick.Automaton()
    for index, word in enumerate(wordlist):
        actree.add_word(word, (index, word))
    actree.make_automaton()
    return actree

def check_word(content):
    global filtertree
    if filtertree == []:
        wordlist = []
        with open("key.txt", "r", encoding='utf-8') as f:
            for line in f.readlines():
                keys = line.split("|")
                for key in keys:
                    wordlist.append(key)
        print("init filter tree.")
        filtertree = build_actree(wordlist)
    res = content
    for i in filtertree.iter(content):
        res = res.replace(i[1][1], "**")
    return res

def reset_task():
    users = User.objects.all()
    for user in users:
        user.task = 0
        user.save()
        
        
    


