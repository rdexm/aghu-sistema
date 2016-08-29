package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NodoPOLVO implements Serializable{

	private static final long serialVersionUID = 8536792138343571861L;
	public static final String COD_PACIENTE="codPaciente", IS_HISTORICO="isHistorico", GSO_SEQP="gsoSeqp", FICHA="ficha", 
							   ORIGEM_DIGITALIZACAO = "origemDigitalizacao", INATIVOS = "inativos";
	
	private Integer prontuario;
	private String tipo;
	private Integer ordem;
	private String descricao;
	private String descricaoTitle;
	private String pagina;
	private String icone;
	private Boolean nodoMessage=false;
	private Boolean quebraLinha=false;
	private Boolean abreTab=false;
	private String nomePaciente;
	private List<NodoPOLVO> nodos=new ArrayList<NodoPOLVO>();
	private Map<String, Object> parametros=new HashMap<>();
	private Boolean ativo=true;
	
	public NodoPOLVO() {
		super();
	}
	
	public NodoPOLVO(Integer prontuario, String tipo,
			String descricao, String icone,  String pagina) {
		super();
		this.prontuario = prontuario;
		this.tipo = tipo;
		this.descricao = descricao;
		this.pagina = pagina;
		this.icone = icone;
	}	
	
	public NodoPOLVO(Integer prontuario, String tipo,
			String descricao, String icone,  Integer ordem, String pagina) {
		super();
		this.prontuario = prontuario;
		this.tipo = tipo;
		this.ordem = ordem;
		this.descricao = descricao;
		this.pagina = pagina;
		this.icone = icone;
	}

	public NodoPOLVO(Integer prontuario, String tipo,
			String descricao, String icone, Boolean nodoMessage) {
		super();
		this.prontuario = prontuario;
		this.tipo = tipo;
		this.descricao = descricao;
		this.icone = icone;
		this.nodoMessage = nodoMessage;
	}	
	
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Integer getOrdem() {
		return ordem;
	}
	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getPagina() {
		return pagina;
	}
	public void setPagina(String pagina) {
		this.pagina = pagina;
	}
	public String getIcone() {
		return icone;
	}
	public void setIcone(String icone) {
		this.icone = icone;
	}
	public List<NodoPOLVO> getNodos() {
		return nodos;
	}
	public void setNodos(List<NodoPOLVO> nodos) {
		this.nodos = nodos;
	}
	public Map<String, Object> getParametros() {
		return parametros;
	}
	public void setParametros(Map<String, Object> parametros) {
		this.parametros = parametros;
	}
	public void addParam(String key, Object value){
		this.parametros.put(key, value);
	}
	
	public void addNodos(NodoPOLVO nodo){
		this.nodos.add(nodo);
	}

	public void addNodos(List<NodoPOLVO> nodos){
		this.nodos.addAll(nodos);
	}	

	public Boolean getNodoMessage() {
		return nodoMessage;
	}

	public void setNodoMessage(Boolean nodoMessage) {
		this.nodoMessage = nodoMessage;
	}

	public String getDescricaoTitle() {
		if (descricaoTitle==null){
			return descricao;
		}
		return descricaoTitle;
	}

	public void setDescricaoTitle(String descricaoTitle) {
		this.descricaoTitle = descricaoTitle;
	}

	public Boolean getAbreTab() {
		return abreTab;
	}

	public void setAbreTab(Boolean abreTab) {
		this.abreTab = abreTab;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ordem == null) ? 0 : ordem.hashCode());
		result = prime * result
				+ ((prontuario == null) ? 0 : prontuario.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof NodoPOLVO)){
			return false;
		}	
		NodoPOLVO other = (NodoPOLVO) obj;
		if (ordem == null) {
			if (other.ordem != null){
				return false;
			}	
		} else if (!ordem.equals(other.ordem)){
			return false;
		}	
		if (prontuario == null) {
			if (other.prontuario != null){
				return false;
			}	
		} else if (!prontuario.equals(other.prontuario)){
			return false;
		}	
		if (tipo == null) {
			if (other.tipo != null){
				return false;
			}	
		} else if (!tipo.equals(other.tipo)){
			return false;
		}	
		return true;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getQuebraLinha() {
		return quebraLinha;
	}

	public void setQuebraLinha(Boolean quebraLinha) {
		this.quebraLinha = quebraLinha;
	}
}