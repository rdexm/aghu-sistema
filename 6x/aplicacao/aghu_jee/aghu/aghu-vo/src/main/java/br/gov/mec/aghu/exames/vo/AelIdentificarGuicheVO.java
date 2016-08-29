package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AelIdentificarGuicheVO implements BaseBean {

	private static final long serialVersionUID = 4901053783046041753L;

	private Short seq;
	private Integer version;
	private String descricao;
	private DominioSimNao ocupado;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private AghUnidadesFuncionais unidadeFuncional;
	private String seqAndarAlaDescricao;
	
	private String nmUnidade;
	private String machine;
	private String usuario;
	private String nomeUsuario;

	public AelIdentificarGuicheVO() {
	}

	public AelIdentificarGuicheVO(Short seq, Integer version, String descricao,
			DominioSimNao ocupado, DominioSituacao indSituacao, Date criadoEm,
			RapServidores servidor, AghUnidadesFuncionais unidadeFuncional,
			String machine, String usuario) {
		super();
		this.seq = seq;
		this.version = version;
		this.descricao = descricao;
		this.ocupado = ocupado;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.unidadeFuncional = unidadeFuncional;
		this.machine = machine;
		this.usuario = usuario;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getOcupado() {
		return ocupado;
	}

	public void setOcupado(DominioSimNao ocupado) {
		this.ocupado = ocupado;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public String getMachine() {
		return machine;
	}

	public void setMachine(String machine) {
		this.machine = machine;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
	
	public String getSeqAndarAlaDescricao() {
		return seqAndarAlaDescricao;
	}
	
	public void setSeqAndarAlaDescricao(String seqAndarAlaDescricao) {
		this.seqAndarAlaDescricao = seqAndarAlaDescricao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AelIdentificarGuicheVO other = (AelIdentificarGuicheVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	public String getNmUnidade() {
		return nmUnidade;
	}

	public void setNmUnidade(String nmUnidade) {
		this.nmUnidade = nmUnidade;
	}

}
