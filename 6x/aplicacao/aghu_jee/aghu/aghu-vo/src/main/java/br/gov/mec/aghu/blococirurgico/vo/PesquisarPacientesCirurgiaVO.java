package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.utils.DateUtil;

public class PesquisarPacientesCirurgiaVO implements Serializable {

	private static final long serialVersionUID = 6054894942437282083L;
	
	private Integer agdSeq;
	private Integer crgSeq;
	private String codigoContexto;
	private String descricaoContexto;
	private String motivoCancContexto;
	private String siglaLocal;
	private String descricaoLocal;
	private String siglaEspecialidade;
	private String nomeEspecialidade;
	private String equipe;
	private String dtAgenda;
	private String procedimentoPrincipal;
	private Boolean indExclusao;
	
	public String getCodigoContexto() {
		return codigoContexto;
	}
	public void setCodigoContexto(String codigoContexto) {
		this.codigoContexto = codigoContexto;
	}
	public String getDescricaoContexto() {
		return descricaoContexto;
	}
	public void setDescricaoContexto(String descricaoContexto) {
		this.descricaoContexto = descricaoContexto;
	}
	public String getMotivoCancContexto() {
		return motivoCancContexto;
	}
	public void setMotivoCancContexto(String motivoCancContexto) {
		this.motivoCancContexto = motivoCancContexto;
	}
	public String getSiglaLocal() {
		return siglaLocal;
	}
	public void setSiglaLocal(String siglaLocal) {
		this.siglaLocal = siglaLocal;
	}
	public String getDescricaoLocal() {
		return descricaoLocal;
	}
	public void setDescricaoLocal(String descricaoLocal) {
		this.descricaoLocal = descricaoLocal;
	}
	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}
	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getDtAgenda() {
		return dtAgenda;
	}
	public void setDtAgenda(String dtAgenda) {
		this.dtAgenda = dtAgenda;
	}
	public String getProcedimentoPrincipal() {
		return procedimentoPrincipal;
	}
	public void setProcedimentoPrincipal(String procedimentoPrincipal) {
		this.procedimentoPrincipal = procedimentoPrincipal;
	}
	public Integer getAgdSeq() {
		return agdSeq;
	}
	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}
	public Integer getCrgSeq() {
		return crgSeq;
	}
	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}
	public Boolean getIndExclusao() {
		return indExclusao;
	}
	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}
	//formata para ordenar data
	public String getDtAgendaOrder() {
		if(dtAgenda != null){
			Date dt;
			try {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				dt = dateFormat.parse(dtAgenda);
				//dt = DateFormat.getDateInstance().parse(dtAgenda);
				return DateUtil.dataToString(dt,"yyyyMMdd");
			} catch(ParseException e){
				// se data mal formatada sera ordenada como ultima data
				return "99";
			}
		}
		// se data null sera ordenada como ultima data
		return "99";
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.getAgdSeq());
		builder.append(this.getCrgSeq());		
		return builder.toHashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (!(obj instanceof PesquisarPacientesCirurgiaVO)){
			return false;
		}
		PesquisarPacientesCirurgiaVO other = (PesquisarPacientesCirurgiaVO) obj;
		
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(this.getAgdSeq(), other.getAgdSeq());
		builder.append(this.getCrgSeq(), other.getCrgSeq());
		return builder.isEquals();
		
//		if (agdSeq == null){
//			if (other.agdSeq != null){
//				if(crgSeq == null){
//					if(other.crgSeq != null){
//						return false;
//					}
//				}else{
//					if(!crgSeq.equals(other.crgSeq)){
//						return false;
//					}
//				}
//			}else{
//				if(crgSeq == null){
//					if(other.crgSeq != null){
//						return false;
//					}
//				}else{
//					if(!crgSeq.equals(other.crgSeq)){
//						return false;
//					}
//				}
//			}
//		} else {
//			if (!agdSeq.equals(other.agdSeq)){
//				if(crgSeq == null){
//					if(other.crgSeq != null){
//						return false;
//					}
//				}else{
//					if(!crgSeq.equals(other.crgSeq)){
//						return false;
//					}
//				}
//			}else{
//				if(crgSeq == null){
//					if(other.crgSeq != null){
//						return false;
//					}
//				}
//			}
//		}
//		return true;
	}
	
	
}