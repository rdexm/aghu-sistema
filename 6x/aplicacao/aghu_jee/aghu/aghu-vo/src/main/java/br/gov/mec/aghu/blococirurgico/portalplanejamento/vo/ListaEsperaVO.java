package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ListaEsperaVO implements BaseBean {

	private static final long serialVersionUID = 7800964080775971145L;

	private String pesNomeUsual;
	private String vpeDescricao;
	private DominioRegimeProcedimentoCirurgicoSus regime;
	private Date dthrInclusao;
	private Integer seq;
	private String especialidadeNome;
	private String especialidadeSigla;
	private String pacienteNome;
	private Integer pacienteProntuario;

	public enum Fields {
		PES_NOME_USUAL("pesNomeUsual"),
		VPE_DESCRICAO("vpeDescricao"),
		REGIME("regime"),
		SEQ("seq"),
		ESPECIALIDADE_NOME("especialidadeNome"),
		ESPECIALIDADE_SIGLA("especialidadeSigla"),
		PACIENTE_NOME("pacienteNome"),
		PACIENTE_PRONTUARIO("pacienteProntuario");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}
		
	}

	public static Fields getFieldByDesc(String desc) {
		for(Fields field : Fields.values()) {
			if(field.toString().equals(desc)) {
				return field;
			}
		}
		return null;
	}

	public String getPesNomeUsual() {
		return pesNomeUsual;
	}
	
	public void setPesNomeUsual(String pesNomeUsual) {
		this.pesNomeUsual = pesNomeUsual;
	}
	
	public String getVpeDescricao() {
		return vpeDescricao;
	}

	public void setVpeDescricao(String vpeDescricao) {
		this.vpeDescricao = vpeDescricao;
	}

	public DominioRegimeProcedimentoCirurgicoSus getRegime() {
		return regime;
	}

	public void setRegime(DominioRegimeProcedimentoCirurgicoSus regime) {
		this.regime = regime;
	}

	public Date getDthrInclusao() {
		return dthrInclusao;
	}

	public void setDthrInclusao(Date dthrInclusao) {
		this.dthrInclusao = dthrInclusao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getEspecialidadeSigla() {
		return especialidadeSigla;
	}

	public void setEspecialidadeSigla(String especialidadeSigla) {
		this.especialidadeSigla = especialidadeSigla;
	}

	public String getPacienteNome() {
		return pacienteNome;
	}

	public void setPacienteNome(String pacienteNome) {
		this.pacienteNome = pacienteNome;
	}

	public Integer getPacienteProntuario() {
		return pacienteProntuario;
	}

	public void setPacienteProntuario(Integer pacienteProntuario) {
		this.pacienteProntuario = pacienteProntuario;
	}

	public String getEspecialidadeNome() {
		return especialidadeNome;
	}

	public void setEspecialidadeNome(String especialidadeNome) {
		this.especialidadeNome = especialidadeNome;
	}
}
