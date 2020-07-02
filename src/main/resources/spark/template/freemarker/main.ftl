<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- In real-world webapps, css is usually minified and
         concatenated. Here, separate normalize from our code, and
         avoid minification for clarity. -->
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/html5bp.css">
    <link rel="stylesheet" href="css/main.css">
    <link rel="stylesheet" href="css/map_data.css">
    <link rel="stylesheet" href="css/shop.css">
    <link rel="shortcut icon" href="css/images/favicon.ico">
  </head>
  <body>
     ${content}
     
     <!-- Again, we're serving up the unminified source for clarity. -->
     <script src="js/jquery-2.1.1.js"></script>
     ${scripts!}
     <!-- sweetalert for fancy looking alerts -->
     <script src="https://cdn.jsdelivr.net/npm/sweetalert2@9"></script>
     <!-- Note: when deploying, replace "development.js" with "production.min.js". -->
     <script src="https://unpkg.com/react@16/umd/react.development.js" crossorigin></script>
     <script src="https://unpkg.com/react-dom@16/umd/react-dom.development.js" crossorigin></script>
     <script src="https://unpkg.com/babel-standalone@6/babel.min.js"></script>
     <!-- GSAP for animations -->
     <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.3.4/gsap.min.js"></script>
	 <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.3.4/MotionPathPlugin.min.js"></script>
	 <script src="js/gsap/CustomEase.min.js"></script>
     <!-- Load React components & helper functions. -->
     <script src="js/dnd/DragItem.js" type="text/babel"></script>
     <script src="js/dnd/DropSlot.js" type="text/babel"></script>
     <script src="js/AddingFriends.js"></script>
     <script src="js/map_creation.js"></script>
     <script src="js/CreateAccount.js"></script>
     <script src="js/ShowingMap.js"></script>
     <script src="js/userhomepage.js" type="text/babel"></script>
  </body>
  <!-- See http://html5boilerplate.com/ for a good place to start
       dealing with real world issues like old browsers.  -->
</html>