let indexJs;

$(function() {

    indexJs = {
        v: {
        },

        c: {
            capital : function(param) {
                $.ajax({
                    url : '/api/capital',
                    method : 'get',
                    data : { 'param' : param },
                    beforeSend: function() {
                        indexJs.f.loadOn();
                    },
                    complete: function() {
                        indexJs.f.loadOff();
                    },
                    success : function(d) {
                        $('#capital textarea').html('');
                        $('#capital textarea').html(d);
                    },
                    error : function(e) {
                        console.log(e);
                    }
                });
            },

            fc : function(param) {
                $.ajax({
                    url : '/api/fc',
                    method : 'get',
                    data : { 'param' : param },
                    beforeSend: function() {
                        indexJs.f.loadOn();
                    },
                    complete: function() {
                        indexJs.f.loadOff();
                    },
                    success : function(d) {
                        $('#fc textarea').html('');
                        $('#fc textarea').html(d);
                    },
                    error : function(e) {
                        console.log(e);
                    }
                });
            },

            image : function(param) {
                $.ajax({
                    url : '/api/image',
                    method : 'get',
                    data : { 'param' : param },
                    beforeSend: function() {
                        indexJs.f.loadOn();
                    },
                    complete: function() {
                        indexJs.f.loadOff();
                    },
                    success : function(d) {
                        $('#image textarea').html('');
                        $('#image textarea').html(d);
                    },
                    error : function(e) {
                        console.log(e);
                    }
                });
            },

            analysis : function(formData) {
                $.ajax({
                    url : '/api/analysis',
                    method : 'post',
                    data : formData,
                    processData : false,	// data 파라미터 강제 string 변환 방지
                    contentType : false,	// application/x-www-form-urlencoded; 방지
                    beforeSend: function() {
                        indexJs.f.loadOn();
                    },
                    complete: function() {
                        indexJs.f.loadOff();
                    },
                    success : function(d) {
                        $('#analysis textarea').html('');
                        $('#analysis textarea').html(d);
                    },
                    error : function(e) {
                        console.log(e);
                    }
                });
            },

            chat : function(param) {
                $.ajax({
                    url : '/api/chat',
                    method : 'get',
                    data : { 'param' : param },
                    beforeSend: function() {
                        indexJs.f.loadOn();
                    },
                    complete: function() {
                        indexJs.f.loadOff();
                    },
                    success : function(d) {
                        $('#chat textarea').html('');
                        $('#chat textarea').html(d);
                    },
                    error : function(e) {
                        console.log(e);
                    }
                });
            },

            documentAdd : function(formData) {
                $.ajax({
                    url : '/api/document',
                    method : 'post',
                    data : formData,
                    processData : false,	// data 파라미터 강제 string 변환 방지
                    contentType : false,	// application/x-www-form-urlencoded; 방지
                    beforeSend: function() {
                        indexJs.f.loadOn();
                    },
                    complete: function() {
                        indexJs.f.loadOff();
                    },
                    success : function(d) {
                        $('#documentAdd textarea').html('success');
                    },
                    error : function(e) {
                        $('#documentAdd textarea').html(e);
                    }
                });
            },

            collectionClear : function() {
                $.ajax({
                    url : '/api/collection',
                    method : 'delete',
                    beforeSend: function() {
                        indexJs.f.loadOn();
                    },
                    complete: function() {
                        indexJs.f.loadOff();
                    },
                    success : function(d) {
                        $('#collectionClear textarea').html('success');
                    },
                    error : function(e) {
                        $('#collectionClear textarea').html(e);
                    }
                });
            },

            documentSearch : function(param, id) {
                $.ajax({
                    url : '/api/document',
                    method : 'get',
                    data : {
                        'param' : param,
                        'id' :  id
                    },
                    beforeSend: function() {
                        indexJs.f.loadOn();
                    },
                    complete: function() {
                        indexJs.f.loadOff();
                    },
                    success : function(d) {
                        $('#documentSearch textarea').html('');
                        $('#documentSearch textarea').html(d);
                    },
                    error : function(e) {
                        $('#documentSearch textarea').html(e);
                    }
                });
            },
        },

        f: {
            loadOn : function() {
                $('#loading').css('display', '');
            },

            loadOff : function() {
                $('#loading').css('display', 'none');
            },

            capital : function() {
                let param = $('#capital input').val();
                indexJs.c.capital(param);
            },

            fc : function() {
                let param = $('#fc input').val();
                indexJs.c.fc(param);
            },

            image : function() {
                let param = $('#image input').val();
                indexJs.c.image(param);
            },

            analysis : function() {
                let formData = new FormData();
                const file = document.getElementById("file").files;
                if (file[0] != null) {
                    formData.append("file", file[0]);
                    indexJs.c.analysis(formData);
                }
            },

            chat : function() {
                let param = $('#chat input').val();
                indexJs.c.chat(param);
            },

            documentAdd : function() {
                let formData = new FormData();
                const file = document.getElementById("file2").files;
                if (file[0] != null) {
                    formData.append("file", file[0]);
                    indexJs.c.documentAdd(formData);
                }
            },

            documentSearch : function() {
                let param = $('#documentSearch #searchParam').val();
                let id = $('#documentSearch #searchId').val();
                indexJs.c.documentSearch(param, id);
            },

            submit : function($parentId) {
                if (indexJs.f.validation($parentId)) {
                    if ($parentId === 'capital') indexJs.f.capital();
                    if ($parentId === 'fc') indexJs.f.fc();
                    if ($parentId === 'image') indexJs.f.image();
                    if ($parentId === 'analysis') indexJs.f.analysis();
                    if ($parentId === 'chat') indexJs.f.chat();
                    if ($parentId === 'documentAdd') indexJs.f.documentAdd();
                    if ($parentId === 'documentSearch') indexJs.f.documentSearch();
                }
            },

            validation : function(id) {
                if ($('#' + id + ' input').val() != '') {
                    return true;
                } else {
                    return false;
                }
            },

        },

        event: function() {
            $('button[name=submit]').on('click', function(e) {
                let $parentId = $(e.currentTarget).parent('.wrap').attr('id');
                indexJs.f.submit($parentId);
            });

            $("#file").on('change',function(){
                const file = document.getElementById("file").files;
                $("#analysis .upload-name").val(file[0].name);
            });

            $("#file2").on('change',function(){
                const file = document.getElementById("file2").files;
                $("#documentAdd .upload-name").val(file[0].name);
            });

            $("input").on('keyup',function(e){
                let $parentId = $(e.currentTarget).parent('.filebox').parent('.wrap').attr('id');
                if (window.event.keyCode == 13) {
                    indexJs.f.submit($parentId);
                }
            });

            $('button[name=collectionClear]').on('click', function(e) {
                indexJs.c.collectionClear();
            });
        },

        init: function() {
            indexJs.event();
        }
    }

    indexJs.init();

});
