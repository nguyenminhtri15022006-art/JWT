$(document).ready(function() {
    // Neu dang o trang profile thi goi API /users/me va /users/
    if (window.location.pathname.indexOf('/user/profile') !== -1) {
        if (!localStorage.token) {
            alert("Sorry, you are not logged in.");
            window.location.href = "/login";
            return;
        }

        // 1. Goi API /users/me lay thong tin nguoi dung hien tai
        loadUserProfile();

        // 2. Goi API /users/ lay danh sach tat ca nguoi dung trong he thong
        loadAllUsers();
    }
});

// Ham lay thong tin nguoi dung hien tai qua JWT (/users/me)
function loadUserProfile() {
    $.ajax({
        type: 'GET',
        url: '/users/me',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        beforeSend: function (xhr) {
            if (localStorage.token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
            }
        },
        success: function(data) {
            $('#profile').html(data.fullName);
            if ($('#emailDisplay').length) {
                $('#emailDisplay').html(data.email);
            }
            if (data.images) {
                $('#images').attr("src", data.images);
            }
            if ($('#tokenDisplay').length) {
                $('#tokenDisplay').text(localStorage.token);
            }
        },
        error: function(e) {
            alert("Sorry, you are not logged in or token has expired.");
            localStorage.clear();
            window.location.href = "/login";
        }
    });
}

// Ham lay danh sach tat ca nguoi dung qua JWT (/users/)
function loadAllUsers() {
    $.ajax({
        type: 'GET',
        url: '/users/',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        beforeSend: function (xhr) {
            if (localStorage.token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
            }
        },
        success: function(users) {
            var tbody = $('#usersTableBody');
            tbody.empty();
            if (users.length === 0) {
                tbody.append('<tr><td colspan="5" class="text-muted">Chưa có người dùng nào</td></tr>');
                return;
            }
            users.forEach(function(u) {
                var imgTag = u.images ? '<img src="' + u.images + '" width="36" height="36" class="rounded-circle border">' : '<span class="text-muted">Chưa có</span>';
                var row = '<tr>' +
                    '<td><strong>' + u.id + '</strong></td>' +
                    '<td class="text-start ps-3 fw-semibold">' + u.fullName + '</td>' +
                    '<td class="text-start ps-3">' + u.email + '</td>' +
                    '<td>' + imgTag + '</td>' +
                    '<td><span class="badge bg-success">Hoạt động</span></td>' +
                    '</tr>';
                tbody.append(row);
            });
        },
        error: function() {
            $('#usersTableBody').html('<tr><td colspan="5" class="text-danger">Không thể tải danh sách người dùng. Token không hợp lệ hoặc đã hết hạn!</td></tr>');
        }
    });
}

// Nut tai lai danh sach users
$('#btnReloadUsers').click(function() {
    loadAllUsers();
});

// Ham dang xuat
$('#logout').click(function() {
    localStorage.clear();
    window.location.href = "/login";
});

// Ham Login (/auth/login)
$('#login').click(function() {
    var email = document.getElementById('email').value.trim();
    var password = document.getElementById('password').value;
    if (!email || !password) {
        alert("Vui lòng nhập đầy đủ Email và Password");
        return;
    }

    var basicInfo = JSON.stringify({
        email: email,
        password: password
    });

    $.ajax({
        type: "POST",
        url: "/auth/login",
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        data: basicInfo,
        success: function(data) {
            localStorage.token = data.token;
            window.location.href = "/user/profile";
        },
        error: function(xhr) {
            var msg = "Đăng nhập thất bại! Kiểm tra lại thông tin.";
            try {
                var res = JSON.parse(xhr.responseText);
                if (res.description) msg = res.description;
            } catch(e) {}
            alert(msg);
        }
    });
});

// Ham Dang ky tai khoan moi (/auth/signup)
$('#btnRegister').click(function() {
    var fullName = $('#regFullName').val().trim();
    var email = $('#regEmail').val().trim();
    var password = $('#regPassword').val();

    if (!fullName || !email || !password) {
        alert("Vui lòng điền đầy đủ Họ và tên, Email và Mật khẩu!");
        return;
    }

    var registerData = JSON.stringify({
        fullName: fullName,
        email: email,
        password: password
    });

    $.ajax({
        type: "POST",
        url: "/auth/signup",
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        data: registerData,
        success: function(user) {
            alert("Đăng ký tài khoản thành công cho " + user.fullName + "! Hãy đăng nhập ngay.");
            // Dien san email va mat khau qua tab dang nhap
            $('#email').val(email);
            $('#password').val(password);
            // Chuyen tab ve Dang nhap
            var loginTabTrigger = new bootstrap.Tab(document.querySelector('#login-tab'));
            loginTabTrigger.show();
        },
        error: function(xhr) {
            var msg = "Đăng ký thất bại! Email có thể đã tồn tại.";
            try {
                var res = JSON.parse(xhr.responseText);
                if (res.description) msg = res.description;
            } catch(e) {}
            alert(msg);
        }
    });
});
