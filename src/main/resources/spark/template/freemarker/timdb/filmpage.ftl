<#assign content>

<h1> Welcome to the Goop Cult </h1>
<a href="/timdb">Back to tIMDb home</a>

<h3>${name}</h3>

<p>Actors in cast:</p>
<ul>
<#list actors as actor>
 <li><a href="/timdb/actor/${actor.id}">${actor.name}</a> 
</#list>
</ul>

</#assign>
<#include "../main.ftl">