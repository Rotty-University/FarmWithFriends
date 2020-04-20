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
  </head>
  <body>
     ${content}
     <!-- Again, we're serving up the unminified source for clarity. -->
     <script src="js/jquery-2.1.1.js"></script>
     ${scripts!}
     <!-- Note: when deploying, replace "development.js" with "production.min.js". -->
     <script src="https://unpkg.com/react@16/umd/react.development.js" crossorigin></script>
     <script src="https://unpkg.com/react-dom@16/umd/react-dom.development.js" crossorigin></script>
     <script src="https://unpkg.com/babel-standalone@6/babel.min.js"></script>
     <!-- Load React components. -->
     <script src="js/userhomepage.js" type="text/babel"></script>
  </body>
  <!-- See http://html5boilerplate.com/ for a good place to start
       dealing with real world issues like old browsers.  -->
</html>