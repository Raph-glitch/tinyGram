<<<<<<< HEAD

=======
var HomePage = {
    view: function (){
        return [m(Home),m(Header)]
    }
}
>>>>>>> 6ea066e... ajout pageabonnements init
var Home = {
    oncreate: function(){
        console.log("Calling render");
        gapiRender("sign-in-login");
    },
    view: function(){
        return m('div',[
            m('div', {id:'leftContainer'},
                m('img', {class:'leftFrontImg', src:'./img/frontImg.jpg', alt:'TinyInsta front page image'}),
            ),
            m('div', {id:'rightContainer'},
                m('h1', {class:'rightTitle'}, "TinyGram"),
                m('div', {id : "sign-in-login"}),
                //m(m.route.Link,{href: "/profile/"+emailToUniqueName(controller.currentUser.getBasicProfile().getEmail()), class:'btn'},"Profile"),
                
                m(m.route.Link,{href: "/profile/tenaudraphael", class: 'buttonSkin', id:'rightButton'},"Profile"),
                
            ),
            m('div', {class:'signatureContainer'},[
                m.trust('<p>' +
                    'Louis NORMAND, Raphaël TENAUD, Sylvain BEAUDOIN, Thibault COLIN  -Web & Cloud-</p>'),
                ]),
        ])
    }
}

var Header = {
    view: function(){
        return m('div',{id: 'headerContainer'},[
                  m('div',{id:'headerLogo'},'TinyGram'),
                  m("button", {class:'buttonSkin', id:'headerDisconnectButton', onclick: function() {
                	  controller.disconnectUser();
                  }},"Déconnexion"),
                      m(m.route.Link,{href:"/timeline", class: 'headerHomeButton'},m('img',{id:'headerHomeButtonImage',src:'./img/logo_maison.png'})
                  ),
                    
                  m('div',{id:'headerInputContainer'},m(SearchBar)),
         ])

    }
}




