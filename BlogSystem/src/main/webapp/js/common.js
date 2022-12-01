// 加上一个逻辑, 通过 GET /login 这个接口来获取下当前的登录状态~
function getUserInfo() {
    $ .ajax({
        type: "get",
        url: "login",
        success: function(body) {
            // 判定此处的 body 是不是一个有效的 user 对象(userId 是否非 0)
            if (body.userId && body.userId > 0) {
                // 登录成功!
                // 不做处理!
                console.log("当前用户登录成功! 用户名: " + body.username);
            } else {
                // 登录失败!
                // 让前端页面, 跳转到 login.html
                alert("当前您尚未登录! 请登录后再访问博客列表!");
                location.assign('blog_login.html');
            }
        },
        error: function() {
            alert("当前您尚未登录! 请登录后再访问博客列表!");
            location.assign('blog_login.html');
        }
    });
}

getUserInfo();