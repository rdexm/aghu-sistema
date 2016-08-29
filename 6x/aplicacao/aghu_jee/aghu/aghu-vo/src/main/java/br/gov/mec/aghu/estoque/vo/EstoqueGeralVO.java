package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.DateUtil;

public class EstoqueGeralVO implements BaseBean {
	
	private static final long serialVersionUID = -2394484523090797751L;
	
	private Integer matCodigo;
	private Date dtCompetencia;
	private Integer frnNumero;
	private String mesAno;
	
	private ScoMaterial material;
	private ScoFornecedor fornecedor;
	private ScoUnidadeMedida unidadeMedida;
	
	private DominioClassifABC classificacaoAbc;
	private DominioClassifABC subClassificacaoAbc;
	
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public Date getDtCompetencia() {
		return dtCompetencia;
	}
	public void setDtCompetencia(Date dtCompetencia) {
		this.dtCompetencia = dtCompetencia;
	}
	public Integer getFrnNumero() {
		return frnNumero;
	}
	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}
	public ScoMaterial getMaterial() {
		return material;
	}
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}
	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public DominioClassifABC getClassificacaoAbc() {
		return classificacaoAbc;
	}
	public void setClassificacaoAbc(DominioClassifABC classificacaoAbc) {
		this.classificacaoAbc = classificacaoAbc;
	}
	public DominioClassifABC getSubClassificacaoAbc() {
		return subClassificacaoAbc;
	}
	public void setSubClassificacaoAbc(DominioClassifABC subClassificacaoAbc) {
		this.subClassificacaoAbc = subClassificacaoAbc;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dtCompetencia == null) ? 0 : dtCompetencia.hashCode());
		result = prime * result + ((frnNumero == null) ? 0 : frnNumero.hashCode());
		result = prime * result + ((matCodigo == null) ? 0 : matCodigo.hashCode());
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
		if (getClass() != obj.getClass()){
			return false;
		}
		EstoqueGeralVO other = (EstoqueGeralVO) obj;
		if (dtCompetencia == null) {
			if (other.dtCompetencia != null){
				return false;
			}
		} else if (!dtCompetencia.equals(other.dtCompetencia)){
			return false;
		}
		if (frnNumero == null) {
			if (other.frnNumero != null){
				return false;
			}
		} else if (!frnNumero.equals(other.frnNumero)){
			return false;
			}
		if (matCodigo == null) {
			if (other.matCodigo != null){
				return false;
			}
		} else if (!matCodigo.equals(other.matCodigo)){
			return false;
		}
		return true;
	}

	public String getMesAno() {
		mesAno = DateUtil.dataToString(dtCompetencia, "MM/yyyy");
		return mesAno;
	}
	public void setMesAno(String mesAno) {
		this.mesAno = mesAno;
	}

	public enum Fields {
		
		MAT_CODIGO("matCodigo"),
		DT_COMPETENCIA("dtCompetencia"),
		FRN_NUMERO("frnNumero"),
		MATERIAL("material"),
		FORNECEDOR("fornecedor"),
		UNIDADE_MEDIDA("unidadeMedida");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}
