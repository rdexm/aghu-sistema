package br.gov.mec.aghu.model;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    ####  
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ## 
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ## 
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ## 
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   ####  
 * ================================================================================
 * 
 * NÃO mapear esta classe para a tabela CSE_PERFIS. 
 * Já foi realizada uma refatoração para a troca de chamadas desta classe
 * para utilizar os serviço de segurança do AGHU:
 * http://redmine.mec.gov.br/projects/aghu/repository/revisions/82769
 *  
 * No lugar, devem ser utilizados os métodos disponíveis em: 
 * br.gov.mec.aghu.casca.service.CascaService
 */
@Deprecated
public class CsePerfis {
	
	private CsePerfis() {
	}
}
