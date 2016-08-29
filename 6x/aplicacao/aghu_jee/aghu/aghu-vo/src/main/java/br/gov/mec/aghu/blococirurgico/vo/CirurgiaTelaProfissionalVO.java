package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VRapServidorConselho;

/**
 * VO da listagem de profissionais na estória #22460 – Agendar procedimentos eletivo, urgência ou emergência
 * 
 * @author aghu
 * 
 */
public class CirurgiaTelaProfissionalVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5083509500870233136L;
	private MbcProfCirurgiasId id;
	private Boolean indResponsavel;
	private Boolean indRealizou;
	private DominioFuncaoProfissional funcaoProfissional;
	private MbcCirurgias cirurgia;
	private RapServidores servidorPuc;
	private MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs;
	private VRapServidorConselho servidorConselho;
	private String especialidade;

	public Boolean getIndResponsavel() {
		return indResponsavel;
	}

	public void setIndResponsavel(Boolean indResponsavel) {
		this.indResponsavel = indResponsavel;
	}

	public Boolean getIndRealizou() {
		return indRealizou;
	}

	public void setIndRealizou(Boolean indRealizou) {
		this.indRealizou = indRealizou;
	}

	public DominioFuncaoProfissional getFuncaoProfissional() {
		return funcaoProfissional;
	}

	public void setFuncaoProfissional(DominioFuncaoProfissional funcaoProfissional) {
		this.funcaoProfissional = funcaoProfissional;
	}

	public VRapServidorConselho getServidorConselho() {
		return servidorConselho;
	}

	public void setServidorConselho(VRapServidorConselho servidorConselho) {
		this.servidorConselho = servidorConselho;
	}

	public MbcProfCirurgiasId getId() {
		return id;
	}

	public void setId(MbcProfCirurgiasId id) {
		this.id = id;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public RapServidores getServidorPuc() {
		return servidorPuc;
	}

	public void setServidorPuc(RapServidores servidorPuc) {
		this.servidorPuc = servidorPuc;
	}

	public MbcProfAtuaUnidCirgs getMbcProfAtuaUnidCirgs() {
		return mbcProfAtuaUnidCirgs;
	}

	public void setMbcProfAtuaUnidCirgs(MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs) {
		this.mbcProfAtuaUnidCirgs = mbcProfAtuaUnidCirgs;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	/**
	 * Utilizado no XHTML para que o hascode seja reconhecido como atributo
	 * 
	 * @return
	 */
	public int getHashCode() {
		return this.hashCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		CirurgiaTelaProfissionalVO other = (CirurgiaTelaProfissionalVO) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
