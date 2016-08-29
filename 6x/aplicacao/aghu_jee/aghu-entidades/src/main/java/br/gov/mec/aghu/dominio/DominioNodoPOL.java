package br.gov.mec.aghu.dominio;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * 
 * @author mlcruz
 */
public enum DominioNodoPOL {
	
	DADOS_PACIENTE("dadosPaciente", true),
	HISTORICO_PACIENTE("historicoPaciente", true),
	DIAGNOSTICO("diagnostico", true),
	ATENDIMENTO("atendimento", true),
	INTERNACAO("internacao", true, true),
	DETALHE_INTERNACAO("detalheInternacao", true, true),
	AMBULATORIO("ambulatorio", true, true),
	EMERGENCIA("emergencia", true, true),
	EXAME("exame",true, true),
	EXAME_ORDEM_CRONOLOGICA("exame-ordemCronologica",false, true),
	EXAME_LABORATORIOS_SERVICOS("exame-laboratoriosServicos",false, true),
	EXAME_AMOSTRAS_COLETADAS("exame-amostrasColetadas",false, true),	
	MEDICAMENTOS("medicamento", true, true),
	CIRURGIA("cirurgia", true),
	PROCEDIMENTO("procedimento", true),
	SESSAO_TERAPEUTICA("sessaoTerapeutica", true, true, false),
	HEMOTERAPIA("hemoterapia", true),
	PROJETO_PESQUISA("projetoPesquisa", true),
	NOTAS("nota", true),
	DIGITALIZACAO_DOCS_LEGAIS("digitalizacaoDocsLegais", true),
	PRONTUARIO_PAPEL("digitalizacaoInativos", true),
	DADOS_HISTORICOS("dadoHistorico", true, true, false),
	EXAME_HIST("exameHist", false, true, true),
	EXAME_ORDEM_CRONOLOGICA_HIST("exame-ordemCronologica-hist",false, true),
	EXAME_LABORATORIOS_SERVICOS_HIST("exame-laboratoriosServicos-hist",false, true),
	EXAME_AMOSTRAS_COLETADAS_HIST("exame-amostrasColetadas-hist",false, true),
	MEDICAMENTOS_HIST("medicamento-hist", false, true),	
	DIGITALIZACAO_INATIVOS("documento-digitalizacao", false, false),
	DIGITALIZACAO("digitalizacao", false, false),
	DOCUMENTO_DIGITALIZACAO("documento_digitalizacao", false, false),
	DOCUMENTOS_CERTIFICADOS("documentosCertificados", true, false),
	INFORMACOES_PERINATAIS("informacoesPerinatais", true, false),
	HISTORIA_OBSTETRICA("gestacoes", true, true);
	
	private String tipo;
	private boolean raiz;
	private boolean filhos;
	private boolean openPage;
	
	private DominioNodoPOL(String tipo, boolean raiz) {
		this.tipo = tipo;
		this.raiz = raiz;
		this.openPage=true;
	}

	private DominioNodoPOL(String tipo, Boolean raiz, boolean filhos) {
		this.tipo = tipo;
		this.raiz = raiz;
		this.filhos = filhos;
		this.openPage=true;
	}
	
	private DominioNodoPOL(String tipo, Boolean raiz, boolean filhos, boolean openPage) {
		this.tipo = tipo;
		this.raiz = raiz;
		this.filhos = filhos;
		this.openPage=openPage;
	}	
	
	
	public static DominioNodoPOL createByTipo(String tipo){
		for (DominioNodoPOL dnp : DominioNodoPOL.values()){
			if (dnp.getTipo().equalsIgnoreCase(tipo)){
				return dnp;
			}
		}
		return null;
	}
	
	
	public static String [] getArrayTipo(){
		return getArrayTipo(false);
	}	
	
	public static String [] getArrayTipo(Boolean raiz){
		List<String> list = new ArrayList<>();
		for (DominioNodoPOL nodo : DominioNodoPOL.values()){
			if (!raiz || (raiz && nodo.isRaiz())){ 
				list.add(nodo.getTipo());
			}
		}
		return list.toArray(new String[list.size()]);
	}

	
	public String getTipo() {
		return tipo;
	}

	public boolean isRaiz() {
		return raiz;
	}

	@SuppressWarnings("PMD")
	public boolean hasFilhos() {
		return filhos;
	}

	public boolean isOpenPage() {
		return openPage;
	}
}