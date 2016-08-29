package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.core.utils.DateUtil;

public class RelatorioTransplanteOrgaosSituacaoVO implements Serializable {
	
	private static final long serialVersionUID = 1227235810396639189L;
	
	private String pacNome;
	private Integer seqTransplantes;
	private Integer pacProntuario;
	private String doencaBase;
	private DominioSexo pacSexo;
	private DominioSituacaoTransplante situacao;
	private DominioTipoOrgao tipoOrgao;
	private Date dataIngresso;
	private String observacoes;
	private Short ordemSituacao;
	private Date dataOcorrencia;
	private Date dataNascimento;
	private String idade;
	private Integer permanencia;
	
	public enum Fields {
		
		PAC_NOME("pacNome"),
		SEQ_TRANSPLANTES("seqTransplantes"),
		PAC_PRONTUARIO("pacProntuario"),
		DOENCA_BASE("doencaBase"),
		PAC_SEXO("pacSexo"),
		SITUACAO("situacao"),
		TIPO_ORGAO("tipoOrgao"),
		DATA_INGRESSO("dataIngresso"),
		DATA_OCORRENCIA("dataOcorrencia"),
		DATA_NASCIMENTO("dataNascimento"),
		OBSERVACOES("observacoes");
		
		private String field;
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	private void calcularIdade(){
		Integer idadeDias, idadeMeses, idadeAnos;
		idadeDias = DateUtil.getIdadeDias(dataNascimento);
		if(idadeDias > 29){
			idadeMeses = DateUtil.getIdadeMeses(dataNascimento);
			if(idadeMeses > 11){
				idadeAnos =  DateUtil.getIdade(dataNascimento);
				if(idadeAnos > 1){
					idade = idadeAnos.toString().concat(" anos");
				}else{
					idade = idadeAnos.toString().concat(" ano");
				}
			}else{
				if(idadeMeses > 1){
					idade = idadeMeses.toString().concat(" meses");
				}else{
					idade = idadeMeses.toString().concat(" mês");
				}
			}
		}else{
			if(idadeDias > 1){
				idade = idadeDias.toString().concat(" dias");
			}else{
				idade = idadeDias.toString().concat(" dia");
			}
		}
	}
	
	public String getDescricaoTipoOrgao(){
		return tipoOrgao.getDescricao();
	}
	
	public String getDescricaoSituacaoTransplante(){
		return situacao.retornarDescricaoCompleta();
	}
	
	public String getNameSituacaoTransplante(){
		return situacao.name();
	}
	
	public String getDescricaoPacSexo(){
		if(pacSexo == null){
			return "";
		}
		return pacSexo.getDescricao();
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getSeqTransplantes() {
		return seqTransplantes;
	}

	public void setSeqTransplantes(Integer seqTransplantes) {
		this.seqTransplantes = seqTransplantes;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public String getPacProntuarioFormatado(){
		if(pacProntuario != null){
			String str = pacProntuario.toString().substring(0, pacProntuario.toString().length() - 1);
			return (str.concat("/").concat(pacProntuario.toString().substring(pacProntuario.toString().length() - 1)));
		}else{
			return "";
		}
	}
	
	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}
	
	public String getDoencaBase() {
		return doencaBase;
	}

	public void setDoencaBase(String doencaBase) {
		this.doencaBase = doencaBase;
	}

	public DominioSexo getPacSexo() {
		return pacSexo;
	}

	public void setPacSexo(DominioSexo pacSexo) {
		this.pacSexo = pacSexo;
	}

	public DominioSituacaoTransplante getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacaoTransplante situacao) {
		//O porque destes numeros pode ser encontrado no documento da estoria 48373
		if(DominioSituacaoTransplante.E.equals(situacao)){
			ordemSituacao = 1;
		}else if(DominioSituacaoTransplante.T.equals(situacao)){
			ordemSituacao = 2;
		}else if(DominioSituacaoTransplante.I.equals(situacao)){
			ordemSituacao = 3;
		}else if(DominioSituacaoTransplante.R.equals(situacao)){
			ordemSituacao = 4;
		}else{
			ordemSituacao = 9;
		}
		this.situacao = situacao;
	}
	
	public Short getOrdemSituacao() {
		return ordemSituacao;
	}

	public void setOrdemSituacao(Short ordemSituacao) {
		this.ordemSituacao = ordemSituacao;
	}

	public DominioTipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(DominioTipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public Date getDataIngresso() {
		return dataIngresso;
	}

	public void setDataIngresso(Date dataIngresso) {
		this.permanencia = (int) ((new Date().getTime() - dataIngresso.getTime()) / (1000*60*60*24));
		this.dataIngresso = dataIngresso;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Date getDataOcorrencia() {
		return dataOcorrencia;
	}

	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
		calcularIdade();
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public Integer getPermanencia() {
		return permanencia;
	}

	public void setPermanencia(Integer permanencia) {
		this.permanencia = permanencia;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((seqTransplantes == null) ? 0 : seqTransplantes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;}
		if (obj == null){
			return false;}
		if (getClass() != obj.getClass()){
			return false;}
		RelatorioTransplanteOrgaosSituacaoVO other = (RelatorioTransplanteOrgaosSituacaoVO) obj;
		if (seqTransplantes == null) {
			if (other.seqTransplantes != null){
				return false;}
		} else if (!seqTransplantes.equals(other.seqTransplantes)){
			return false;}
		return true;
	}
}
