<#-- @ftlvariable name="upload" type="ml.denisd3d.m2d.model.Upload" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Mc2Discord Website</title>
    <link rel="stylesheet" href="//unpkg.com/@highlightjs/cdn-assets@11.1.0/styles/mono-blue.min.css">
    <script src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/11.1.0/highlight.min.js"></script>
    <script>hljs.highlightAll();</script>
</head>
<body style="text-align: center; font-family: sans-serif">
<img src="/static/mc2discord.png" alt="Mc2Discord logo">
<h1>Upload : ${upload.id}</h1>
<hr>
<h5>Config : </h5>
<pre style="text-align: left"><code class="language-toml">${upload.config}</code></pre>
<hr>
<h5>Errors : </h5>
<pre style="text-align: left"><code class="language-plaintext">${upload.errors}</code></pre>
<hr>
<h5>Env : </h5>
<pre style="text-align: left"><code class="language-plaintext">${upload.env}</code></pre>

</body>
</html>