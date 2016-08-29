package br.gov.mec.aghu.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

@SuppressWarnings("PMD")
public class AjustadorVO {
	
	
	private static final String PATH_PROJETO = "/home/geraldo/workspace-construcao-4/aghu-6-construcao/";
	private static final String PATH_DESTINO = "/home/geraldo/workspace-kepler/aghu/";
//	private static final String PATH_PROJETO = "/aghu/workspace_5_dev/aghu/";
//	private static final String PATH_DESTINO = "/aghu/workspace_6_dev/aghu/";
	
	private static final String PATH_FONTE_MAVEN = "/src/main/java";
	
	private static Map<String, String> ajustesModulosClientes = new HashMap<>();
	
	private static Map.Entry<String, String> entradaAtual;

	
	static long contador = 0;
	
	static long contadorTotal = 0;

	public static void main(String[] args) throws Exception {
		inicializarMapModulos();
		System.out.println("Iniciando processo de ajuste dos VOs do sistema"); // NOPMD
		for (Map.Entry<String, String> entry : ajustesModulosClientes.entrySet() ){
			System.out.println("Iniciando processo de ajuste dos VOs do modulo: " + entry.getKey()); // NOPMD
			entradaAtual = entry;
			File folderSourceCodeModule = new File (PATH_PROJETO + entry.getKey() + PATH_FONTE_MAVEN);
			processaArquivos(folderSourceCodeModule);
			System.out.println("Ajustados " + contador +  " VOs do modulo: " + entry.getKey()); // NOPMD
			contadorTotal += contador;
			contador = 0;
		}
		System.out.println("ajuste dos VOs concluido com sucesso. total: " + contadorTotal); // NOPMD
	}

	private static void inicializarMapModulos() {
		ajustesModulosClientes.put("base","aghu-vo");
		ajustesModulosClientes.put("vo","aghu-vo");
		ajustesModulosClientes.put("parametrosistema/client","aghu-configuracao-client");
		ajustesModulosClientes.put("casca/client","aghu-casca-client");
		ajustesModulosClientes.put("sicon/client","aghu-sicon-client");
		ajustesModulosClientes.put("compras/client","aghu-compras-client");		
		ajustesModulosClientes.put("perinatologia/client","aghu-perinatologia-client");		
		ajustesModulosClientes.put("controleinfeccao/client","aghu-controleinfeccao-client");		
		ajustesModulosClientes.put("registrocolaborador/client","aghu-registrocolaborador-client");		
		ajustesModulosClientes.put("controlepaciente/client","aghu-controlepaciente-client");		
		ajustesModulosClientes.put("estoque/client","aghu-estoque-client");		
		ajustesModulosClientes.put("prescricaoenfermagem/client","aghu-prescricaoenfermagem-client");		
		ajustesModulosClientes.put("bancosangue/client","aghu-bancosangue-client");		
		ajustesModulosClientes.put("blococirurgico/client","aghu-blococirurgico-client");		
		ajustesModulosClientes.put("checagemeletronica/client","aghu-checagemeletronica-client");		
		ajustesModulosClientes.put("paciente/client","aghu-paciente-client");		
		ajustesModulosClientes.put("farmacia/client","aghu-farmacia-client");		
		ajustesModulosClientes.put("ambulatorio/client","aghu-ambulatorio-client");		
		ajustesModulosClientes.put("internacao/client","aghu-internacao-client");		
		ajustesModulosClientes.put("prescricaomedica/client","aghu-prescricaomedica-client");		
		ajustesModulosClientes.put("centrocusto/client","aghu-centrocusto-client");		
		ajustesModulosClientes.put("orcamento/client","aghu-orcamento-client");		
		ajustesModulosClientes.put("protocolos/client","aghu-protocolos-client");		
		ajustesModulosClientes.put("nutricao/client","aghu-nutricao-client");		
		ajustesModulosClientes.put("exames/client","aghu-exames-client");		
		ajustesModulosClientes.put("faturamento/client","aghu-faturamento-client");		
		ajustesModulosClientes.put("procedimentoterapeutico/client","aghu-procedimentoterapeutico-client");		
		ajustesModulosClientes.put("indicadores/client","aghu-indicadores-client");		
		ajustesModulosClientes.put("administracao/client","aghu-administracao-client");		
		ajustesModulosClientes.put("certificacaodigital/client","aghu-certificacaodigital-client");		
		ajustesModulosClientes.put("configuracao/client","aghu-configuracao-client");		
		ajustesModulosClientes.put("sig/client","aghu-sig-client");		
		ajustesModulosClientes.put("comissoes/client","aghu-comissoes-client");	
		
	}

	private static void processaArquivos(File baseDir) throws Exception {
		File[] files = baseDir.listFiles();

		for (File file : files) {
			if (file.getName().endsWith("svn")){
				continue;
			}
			if (file.isDirectory()) {
				processaArquivos(file);
			} else {
				processarArquivoClasse(file);
			}
		}
	}

	private static void processarArquivoClasse(File file) throws Exception {
	
		if (verificarProcessaArquivo(file)) {
			contador++;
			System.out.println("Processando VO " + file.getName()); // NOPMD			
			
			String stConteudo = FileUtils.readFileToString(file);			

			stConteudo = stConteudo.replace("br.gov.mec.util.DateUtil", "br.gov.mec.aghu.core.utils.DateUtil");
			
			stConteudo = stConteudo.replace("br.gov.mec.aghu.util.AghuUtil", "br.gov.mec.aghu.core.commons.CoreUtil");
			
			stConteudo = stConteudo.replace("br.gov.mec.aghu.util.NumberUtil", "br.gov.mec.aghu.core.commons.NumberUtil");
			
			stConteudo = stConteudo.replace("br.gov.mec.aghu.util.AghuNumberFormat", "br.gov.mec.aghu.core.utils.AghuNumberFormat");
			
			stConteudo = stConteudo.replace("br.gov.mec.aghu.util.DateFormatUtil", "br.gov.mec.aghu.core.utils.DateFormatUtil");
			
			stConteudo = stConteudo.replace("br.gov.mec.util.MecUtil", "br.gov.mec.aghu.core.commons.CoreUtil");
			
			stConteudo = stConteudo.replace("br.gov.mec.aghu.util.StringUtil", "br.gov.mec.aghu.core.utils.StringUtil");
			
			stConteudo = stConteudo.replace("br.gov.mec.aghu.util.ConselhoRegionalMedicinaEnum", "br.gov.mec.aghu.enums.ConselhoRegionalMedicinaEnum");
			
			stConteudo = stConteudo.replace("br.gov.mec.aghu.util.ConselhoRegionalOdontologiaEnum", "br.gov.mec.aghu.enums.ConselhoRegionalOdontologiaEnum");
			
			stConteudo = stConteudo.replace("br.gov.mec.dominio.Dominio", "br.gov.mec.aghu.core.dominio.Dominio");
			
			stConteudo = stConteudo.replace("br.gov.mec.aghu.business.exception.AGHUNegocioException;", "br.gov.mec.aghu.core.exception.ApplicationBusinessException;");
			
			stConteudo = stConteudo.replace("br.gov.mec.aghu.business.exception.AGHUNegocioExceptionSemRollback;", "br.gov.mec.aghu.core.exception.BaseRuntimeException;");
			
			stConteudo = stConteudo.replace("br.gov.mec.seam.business.exception.MECBaseException;", "br.gov.mec.aghu.core.exception.BaseException;");
			
			stConteudo = stConteudo.replace("org.jboss.seam.international.StatusMessage.Severity", "br.gov.mec.aghu.core.exception.Severity");
			
			
			if (stConteudo.contains("NegocioExceptionCode")) {
				stConteudo = stConteudo.replaceFirst("import br.gov.mec.seam.business.exception.NegocioExceptionCode;"
						, "import br.gov.mec.aghu.core.exception.BusinessExceptionCode;");
				
				stConteudo = stConteudo.replace("NegocioExceptionCode", "BusinessExceptionCode");
			}
			
			//br.gov.mec.seam.business.exception.MECBaseException
			
			stConteudo = stConteudo.replace("AGHUNegocioExceptionSemRollback", "BaseRuntimeException");
			
			stConteudo = stConteudo.replace("AGHUNegocioException", "ApplicationBusinessException");
			
			stConteudo = stConteudo.replace("AghuUtil", "CoreUtil");
			
			stConteudo = stConteudo.replace("MecUtil", "CoreUtil");
			
			stConteudo = stConteudo.replace("MECBaseException", "BaseException");
			
			
			
			

		
			
		//	StringBuilder sbConteudo = new StringBuilder(stConteudo);		
			
			
			StringBuilder sb = new StringBuilder(file.getPath());
			
			int index = sb.indexOf(PATH_FONTE_MAVEN);
			
			sb.delete(0, index + PATH_FONTE_MAVEN.length()+1);			

			File fileSaida = new File(PATH_DESTINO + entradaAtual.getValue() + PATH_FONTE_MAVEN + "/"+ sb.toString());

			FileUtils.writeStringToFile(fileSaida,stConteudo);

		}else{
			System.out.println(" - Arquivo não processado: " + file.getName()); // NOPMD
		}

	}

	private static boolean verificarProcessaArquivo(File file) {
		boolean retorno = false;
		
		if (file.getName().endsWith("VO.java")){
			retorno = true;
		}
		if (file.getName().endsWith("Vo.java")){
			retorno = true;
		}
		if (file.getName().endsWith("DTO.java")){
			retorno = true;
		}
		if (file.getName().endsWith("AtualizarPacienteTipo.java")){
			retorno = true;
		}
		if (file.getName().endsWith("CalculoObjetosCentrosCustosInterface.java")){
			retorno = true;
		}
		if (file.getName().endsWith("TipoPrescricaoCuidadoEnfermagem.java")){
			retorno = true;
		}
		if (file.getName().endsWith("SumarioAltaDiagnosticosCidVOComparator.java")){
			retorno = true;
		}
		if (file.getName().endsWith("Cnet.java")){
			retorno = true;
		}
		if (file.getName().endsWith("RelUltimasComprasPACVOPai.java")){
			retorno = true;
		}
		if (file.getName().endsWith("ScoRamoComercialCriteria.java")){
			retorno = true;
		}
		if (file.getName().endsWith("SolicitacaoExameFilter.java")){
			retorno = true;
		}		
		return retorno;
	}

}
