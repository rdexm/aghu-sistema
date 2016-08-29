package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioTipoNodoCustos;
import br.gov.mec.aghu.core.utils.DateConstants;

public class ConsumoPacienteNodoVO implements Serializable {
	
	private static final long serialVersionUID = -5892526152546642742L;
	private static final String ICO_PRESCRICAO_ENFERMAGEM_PNG = "/resources/img/icons/ico-prescricao-enfermagem.png";
	private static final String DATABASE_TABLE_PNG = "/resources/img/icons/database_table.png";
	private static final String OPME_PNG = "/images/opme.png";
	private static final String NUTRICOES_PARENTERAIS_PNG = "/resources/img/icons/cup_link.png";
	private static final String HEMOTERAPIA_PNG = "/resources/img/icons/hemoterapia.png";
	private static final String EXAMES_PNG = "/resources/img/icons/exames.png";
	private static final String DIETA_PNG = "/images/dieta.png";
	private static final String CONSULTORIA_AMBULATORIAL_PNG = "/resources/img/icons/consultoria_ambulatorial.png";
	private static final String CIRURGIAS_PNG = "/resources/img/icons/cirurgias.png";
	private static final String MED_QUIMIOTERAPICOS_PNG = "/resources/img/icons/med_quimioterapicos.png";
	private static final String OBJETO_CUSTO_APOIO_PNG = "/resources/img/icons/atendimentos.png";
	private static final String OUTRAS_RECEITAS_PNG = "/resources/img/icons/objeto-custo-visualizar.png";	
	
	private Integer atdSeq; 
	private Date dthrInicio;
	private String dthrInicioStr;
	private Date dthrFim;
	private String dthrFimStr;
	private String descricao; 
	private Short ordemVisualizacao;
	private DominioIndContagem indContagem;
	private List<ConsumoPacienteNodoVO> listaNodos = new ArrayList<ConsumoPacienteNodoVO>();
	private Boolean ativo=true;
	private String tipoIcone;
	private String agrupador;
	private Integer seqCategoria;
	private Integer prontuario;
	private DominioTipoNodoCustos tipoNodo;
	private Boolean isAgrupador;
	private Integer codigoCentroCusto;
	
	public void setTipoIcone(String tipoIcone) {
		this.tipoIcone = tipoIcone;
	}

	public ConsumoPacienteNodoVO(){}
	
	public ConsumoPacienteNodoVO(
			Integer atdSeq, 
			Date dthrInicio,
			Date dthrFim, 
			String descricao, 
			Short ordemVisualizacao,
			DominioIndContagem indContagem,
			Boolean isAgrupador) {
		super();
		this.atdSeq = atdSeq;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.descricao = descricao;
		this.ordemVisualizacao = ordemVisualizacao;
		this.indContagem = indContagem;
		this.isAgrupador = isAgrupador;
	}


	public enum Fields{
		ATD_SEQ("atdSeq"), 
		DTH_INICIO("dthrInicio"), 
		DTH_FIM("dthrFim"),
		DESCRICAO("descricao"), 
		ORDEM_VISUALIZACAO("ordemVisualizacao"),
		AGRUPADOR("agrupador"),
		SEQ_CATEGORIA("seqCategoria"),
		PRONTUARIO("prontuario"),
		IND_CONTAGEM("indContagem");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	
	public Date getDthrInicio() {
		return dthrInicio;
	}
	
	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
	
	public Date getDthrFim() {
		return dthrFim;
	}
	
	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Short getOrdemVisualizacao() {
		return ordemVisualizacao;
	}
	
	public void setOrdemVisualizacao(Short ordemVisualizacao) {
		this.ordemVisualizacao = ordemVisualizacao;
	}
	public DominioIndContagem getIndContagem() {
		return indContagem;
	}
	
	public void setIndContagem(DominioIndContagem indContagem) {
		this.indContagem = indContagem;
	}

	public void setDthrInicioStr(String dthrInicioStr) {
		this.dthrInicioStr = dthrInicioStr;
	}

	public String getDthrInicioStr() {
		if(dthrInicio != null){
			dthrInicioStr = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY).format(dthrInicio);
			return dthrInicioStr; 
		}
		
		dthrInicioStr = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY).format(new Date());
		return dthrInicioStr; 
	}

	public void setDthrFimStr(String dthrFimStr) {
		this.dthrFimStr = dthrFimStr;
	}

	public String getDthrFimStr() {
		if(dthrFim != null){
			dthrFimStr = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY).format(dthrFim);
			return dthrFimStr;
		}
		dthrFimStr = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY).format(new Date());
		return dthrFimStr;
	}	
	
	public void addNodos(ConsumoPacienteNodoVO nodo){
		this.listaNodos.add(nodo);
	}

	public void addNodos(List<ConsumoPacienteNodoVO> nodos){
		this.listaNodos.addAll(nodos);
	}	

	public String getTipoIcone(){
		if(tipoIcone!=null){
			return tipoIcone;
		}
		switch (this.indContagem) {
		
		case CR:
			return CIRURGIAS_PNG;
		case CS:
			return CONSULTORIA_AMBULATORIAL_PNG;	
		case CE:
			return CONSULTORIA_AMBULATORIAL_PNG;
		case DI:
			return DIETA_PNG;	
		case EX:
			return EXAMES_PNG;
		case HM:
			return HEMOTERAPIA_PNG;	
		case MD:
			return MED_QUIMIOTERAPICOS_PNG;	
		case NP:
			return NUTRICOES_PARENTERAIS_PNG;
		case OP:
			return OPME_PNG;	
		case PE:
			return DATABASE_TABLE_PNG;	
		case QC:
			return DATABASE_TABLE_PNG;
		case QM:
			return MED_QUIMIOTERAPICOS_PNG;
		case DC:
			return ICO_PRESCRICAO_ENFERMAGEM_PNG;
		case DM:
			return MED_QUIMIOTERAPICOS_PNG;
		case AP:
			return OBJETO_CUSTO_APOIO_PNG;
		case OR:
			return OUTRAS_RECEITAS_PNG;
		default:
			return CIRURGIAS_PNG;
		}
	}

	public Integer getSeqCategoria() {
		return seqCategoria;
	}

	public void setSeqCategoria(Integer seqCategoria) {
		this.seqCategoria = seqCategoria;
	}

	public String getAgrupador() {
		return agrupador;
	}

	public void setAgrupador(String agrupador) {
		this.agrupador = agrupador;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public void setListaNodos(List<ConsumoPacienteNodoVO> listaNodos) {
		this.listaNodos = listaNodos;
	}

	public List<ConsumoPacienteNodoVO> getListaNodos() {
		if(this.listaNodos == null){
			this.listaNodos = new ArrayList<ConsumoPacienteNodoVO>();
		}
		return this.listaNodos;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public DominioTipoNodoCustos getTipoNodo() {
		return tipoNodo;
	}

	public void setTipoNodo(DominioTipoNodoCustos tipoNodo) {
		this.tipoNodo = tipoNodo;
	}

	public Boolean getIsAgrupador() {
		return isAgrupador;
	}

	public void setIsAgrupador(Boolean isAgrupador) {
		this.isAgrupador = isAgrupador;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}	
}