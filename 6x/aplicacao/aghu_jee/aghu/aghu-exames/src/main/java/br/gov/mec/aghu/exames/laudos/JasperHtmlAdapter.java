package br.gov.mec.aghu.exames.laudos;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioFontes;

public class JasperHtmlAdapter {

	/**
	 * Método para compatibilizar o html gerado pelo pe:ckeditor (editor html do primefaces)
	 * de modo que o jasper report exiba o mais compativel possivel 
	 * 
	 * Ver limitação de tags quando markup: html
	 * {@link http://jasperreports.sourceforge.net/sample.reference/styledtext/}
	 * 
	 * @param texto
	 * @return
	 */
	public static String jasperHtmlAdapter(String texto) {
		
		texto = normalizaFontes(texto);
		texto = adaptaTagsHtml(texto);
		
		return texto;
	}

	private static String normalizaFontes(String texto) {
		for (DominioFontes dominioFontes : DominioFontes.values()) {
			texto = texto.replace(dominioFontes.getDescricao().toLowerCase(), dominioFontes.getDescricao());
		}
		
		return texto;
	}
	
	

	private static String adaptaTagsHtml(String texto) {
		
		List<HtmlTagsAdapter> tags = new ArrayList<>();
		
		tags.add(new HtmlTagsAdapter("<em>", "<i>"));
		tags.add(new HtmlTagsAdapter("</em>", "</i>"));
		
		tags.add(new HtmlTagsAdapter("<strong>", "<b>"));
		tags.add(new HtmlTagsAdapter("</strong>", "</b>"));
		
		tags.add(new HtmlTagsAdapter("<s>", "<span style=\"text-decoration: line-through;\">"));
		tags.add(new HtmlTagsAdapter("</s>", "</span>"));
		
		for (HtmlTagsAdapter tag : tags) {
			texto = texto.replace(tag.getOrigem(), tag.getDestino());
		}
		
		return texto;
	}
	
}
