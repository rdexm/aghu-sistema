package br.gov.mec.aghu.dao.analyzer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("PMD")
public class DaoAnalyzerMain {
	
	private static final Log LOG = LogFactory.getLog(DaoAnalyzerMain.class);

	public static void main(String[] args) {

//		Jah migrados
		String[] modulos = {"casca", "certificacaodigital", "configuracao", "paciente", "parametrosistema", "registrocolaborador"};
//		Novos modulos
//		String[] modulos = {"integracao", "sicon", "compras", "perinatologia", "controleinfeccao"
//				, "controlepaciente", "estoque", "prescricaoenfermagem", "bancosangue", "blococirurgico"
//				, "checagemeletronica", "farmacia", "ambulatorio", "internacao", "prescricaomedica"
//				, "centrocusto", "orcamento", "protocolos", "nutricao", "exames"
//				, "faturamento", "procedimentoterapeutico", "indicadores", "administracao", "sig"
//				, "comissoes"};

		//copyDAOs(modulos);
		analizeDaos(modulos);
	}
	

	public static void copyDAOs(String[] modulos) {
		DaoAnalyzerCopy copiadora = new DaoAnalyzerCopy("/aghu/workspace_5_dev/aghu", "/aghu/workspace_6_dev/aghu"); 
		
		copiadora.execute(modulos);
	}
	
	public static void analizeDaos(String[] modulos) {
		for (String modulo : modulos) {
			adjustDao(modulo);
		}
	}
	
	private static void adjustDao(String modulo) {
		System.out.println("Ajustando modulo: " + modulo);
		
		LoadClass pacotePaciente = new LoadClass(modulo); 
		
		// Pega todas as classes do pacote definido.
		Class[] listaClasses = pacotePaciente.execute();
		if (listaClasses == null) {
			LOG.info("Nao encontrou classes.");
			return;
		}
		LOG.info("Total: " + listaClasses.length);
		
		DaoAnalyzer aDaoAnalyzer = new DaoAnalyzer(pacotePaciente, listaClasses);
		aDaoAnalyzer.analyse();
		LOG.info(aDaoAnalyzer.getLogMessage());
		
		LOG.info("Feito.");
	}
	
	

}
