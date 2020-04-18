<#assign content>

<h1> Welcome to the Goop Cult </h1>
<a href="/timdb">Back to tIMDb home</a>

<h3>${name}</h3>

<p>Films they appeared in:</p>
<ul>
<#list films as film>
 <li><a href="/timdb/film/${film.id}">${film.name}</a> 
</#list>
</ul>

</#assign>
<#include "../main.ftl">