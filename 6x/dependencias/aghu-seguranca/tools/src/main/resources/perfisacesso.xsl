<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="outputPath" />
<xsl:output method="text" />
<xsl:output method="html" indent="yes" name="html" />
<xsl:template match="/">
<xsl:result-document href="{$outputPath}" format="html">
<html>
<head>
<xsl:comment> Generated via XSLT on <xsl:value-of select="format-dateTime(current-dateTime(), '[F], [MNn] [D], [Y] at [h]:[m01] [P]')" /> </xsl:comment>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Documentação AGHU</title>
<style>
@import url('../css/documentacao.css');
</style>
<script src="../js/tabber.js" type="text/javascript"></script>
<link media="screen" type="text/css" href="../css/tabber.css"
	rel="stylesheet" />
</head>
<body>
<div class="doc_aghu macro_processos"><a
	href="javascript:history.back()">voltar</a>
<h1>Perfis de acesso</h1>

<p>Nesta documentação estão descritos os diferentes perfis de acesso
do AGHU.</p>
<xsl:variable name="dt" select="current-dateTime()"/>
<p> Documentação gerada em: <b><xsl:value-of select="format-dateTime($dt, '[D01]/[M01]/[Y0001] [H01]:[m01]:[s01]', 'en', (), ())"/> </b></p>

<table class="centralizada">
	<tr>
		<th>Nome do Perfil</th>
		<th>Papel</th>
		<th>Descrição</th>
	</tr>
	<xsl:for-each select="seguranca/perfis/perfil">
	<xsl:sort select="@nome"/>	
	<tr>
		<td><xsl:value-of select="@nome"/></td>
		<td><xsl:value-of select="@descricao-resumida"/></td>
		<td><xsl:value-of select="@descricao"/></td>
	</tr>
	</xsl:for-each>
</table>

</div>
</body>
</html>
</xsl:result-document>
</xsl:template>
</xsl:stylesheet>