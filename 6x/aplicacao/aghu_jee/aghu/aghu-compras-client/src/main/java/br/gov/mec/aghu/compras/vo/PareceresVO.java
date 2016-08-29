package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioParecerOcorrencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;


/**
 * Os dados armazenados nesse objeto representam os Itens de uma Licitação
 * 
 * @author Lilian
 */
public class PareceresVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1094758748075863219L;

	private Integer codigoParecerMaterial;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer codigoGrupoMaterial;
	private String descricaoGrupoMaterial;
	private Integer codigoMarcaComercial;
	private String descricaoMarcaComercial;
	private Integer seqpModeloComercial;
	private String descricaoModeloComercial;
	private Integer codigoPasta;
	private String  descricaoPasta; 
	private Integer numeroSubPasta;
	private String nrRegistro;
	private Date dtVenctRegistro;
	private DominioParecer parecerGeral;
	private DominioParecerOcorrencia parecerOcorrecia;
	private Boolean ocorrencia;
	private Date dtParecer;
	private DominioSituacao situacao;
	private String descricaoMaterial;
	private RapServidores servidor;
	
	// GETs and SETs
	
	public Integer getCodigoParecerMaterial() {
		return codigoParecerMaterial;
	}
	public void setCodigoParecerMaterial(Integer codigoParecerMaterial) {
		this.codigoParecerMaterial = codigoParecerMaterial;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}
	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}
	public String getDescricaoGrupoMaterial() {
		return descricaoGrupoMaterial;
	}
	public void setDescricaoGrupoMaterial(String descricaoGrupoMaterial) {
		this.descricaoGrupoMaterial = descricaoGrupoMaterial;
	}
	public Integer getCodigoMarcaComercial() {
		return codigoMarcaComercial;
	}
	public void setCodigoMarcaComercial(Integer codigoMarcaComercial) {
		this.codigoMarcaComercial = codigoMarcaComercial;
	}
	public String getDescricaoMarcaComercial() {
		return descricaoMarcaComercial;
	}
	public void setDescricaoMarcaComercial(String descricaoMarcaComercial) {
		this.descricaoMarcaComercial = descricaoMarcaComercial;
	}
	public Integer getSeqpModeloComercial() {
		return seqpModeloComercial;
	}
	public void setSeqpModeloComercial(Integer seqpModeloComercial) {
		this.seqpModeloComercial = seqpModeloComercial;
	}
	public String getDescricaoModeloComercial() {
		return descricaoModeloComercial;
	}
	public void setDescricaoModeloComercial(String descricaoModeloComercial) {
		this.descricaoModeloComercial = descricaoModeloComercial;
	}
	public Integer getCodigoPasta() {
		return codigoPasta;
	}
	public void setCodigoPasta(Integer codigoPasta) {
		this.codigoPasta = codigoPasta;
	}
	public String getDescricaoPasta() {
		return descricaoPasta;
	}
	public void setDescricaoPasta(String descricaoPasta) {
		this.descricaoPasta = descricaoPasta;
	}
	public Integer getNumeroSubPasta() {
		return numeroSubPasta;
	}
	public void setNumeroSubPasta(Integer numeroSubPasta) {
		this.numeroSubPasta = numeroSubPasta;
	}
	public String getNrRegistro() {
		return nrRegistro;
	}
	public void setNrRegistro(String nrRegistro) {
		this.nrRegistro = nrRegistro;
	}
	public Date getDtVenctRegistro() {
		return dtVenctRegistro;
	}
	public void setDtVenctRegistro(Date dtVenctRegistro) {
		this.dtVenctRegistro = dtVenctRegistro;
	}
	public DominioParecer getParecerGeral() {
		return parecerGeral;
	}
	public void setParecerGeral(DominioParecer parecerGeral) {
		this.parecerGeral = parecerGeral;
	}
	public DominioParecerOcorrencia getParecerOcorrecia() {
		return parecerOcorrecia;
	}
	public void setParecerOcorrecia(DominioParecerOcorrencia parecerOcorrecia) {
		this.parecerOcorrecia = parecerOcorrecia;
	}
	public Boolean getOcorrencia() {
		return ocorrencia;
	}
	public void setOcorrencia(Boolean ocorrencia) {
		this.ocorrencia = ocorrencia;
	}
	public Date getDtParecer() {
		return dtParecer;
	}
	public void setDtParecer(Date dtParecer) {
		this.dtParecer = dtParecer;
	}	
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}




	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}




	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}




	public enum Fields {
		CODIGO_PARECER("codigoParecerMaterial"),
		SITUACAO("situacao"),
		CODIGO_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		CODIGO_GRUPO_MATERIAL("codigoGrupoMaterial"),
		DESCRICAO_GRUPO_MATERIAL("descricaoGrupoMaterial"),
		CODIGO_MARCA_COMERCIAL("codigoMarcaComercial"),
		DESCRICAO_MARCA_COMERCIAL("descricaoMarcaComercial"),
		CODIGO_MODELO_COMERCIAL("seqpModeloComercial"),
		DESCRICAO_MODELO_COMERCIAL("descricaoModeloComercial"),
		CODIGO_PASTA("codigoPasta"),
		NUMERO_SUBPASTA("numeroSubPasta"),	
		NUMERO_REGISTRO("nrRegistro"),
		DT_VENCT_REGISTRO("dtVenctRegistro"),
		PARECER_GERAL("parecerGeral"),
		PARECER_OCORRENCIA("parecerOcorrecia"),
		OCORRENCIA("ocorrencia"),
		DT_PARECER("dtParecer"),
		SERVIDOR("servidor");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}	
			
}
