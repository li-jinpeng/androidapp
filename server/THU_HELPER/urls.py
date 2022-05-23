"""THU_HELPER URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, re_path
from . import views

urlpatterns = [
    path('admin/', admin.site.urls),
    path('user/get/home/', views.get_user_home),
    path('user/get/detail/', views.get_user_detail),
    path('user/init/', views.new_user),
    path('user/login/', views.login),
    path('user/edit/', views.edit_user),
    path('user/task/', views.user_task),
    path('user/store/', views.user_store),
    path('user/draft/', views.user_draft),
    path('user/record/', views.user_record),
    path('user/blacklist/', views.user_blacklist),
    path('user/follow/', views.user_follow),
    path('user/follows/', views.user_follows),
    path('user/followeds/', views.user_followeds),
    path('user/change_password/', views.change_password),
    path('reply/post/', views.post_reply),
    path('message/index/', views.get_message_index),
    path('message/send/', views.send_message),
    path('message/detail/', views.get_message_detail),
    path('message/delete/', views.delete_message),
    path('food/index/', views.get_food_index),
    path('food/detail/', views.get_food_detail),
    path('food/add_score/', views.add_food_score),
    path('food/init/', views.new_food),
    path('post/index/', views.get_post_index),
    path('post/detail/', views.get_post_detail),
    path('post/init/', views.new_post),
    path('post/done/', views.finish_post),
    path('post/good/', views.good_post),
    path('course/index/', views.get_course_index),
    path('course/detail/', views.get_course_detail),
    path('course/add_score/', views.add_course_score),
    path('course/init/', views.new_course),
    path('operator/edit/', views.edit_operator),
    path('operator/search/', views.index_search),
    path('draft/init/', views.new_draft),
    path('pic/<id>/', views.get_image),
    path('get_id/', views.get_id),
    path('judge_identy/', views.judge_identy),
    path('change_identy/',views.change_identy),
    path('get_doc/',views.get_doc),
]
