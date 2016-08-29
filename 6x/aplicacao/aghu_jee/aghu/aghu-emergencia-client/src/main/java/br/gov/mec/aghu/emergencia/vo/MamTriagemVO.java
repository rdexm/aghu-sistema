package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.MamOrigemPaciente;
import br.gov.mec.aghu.model.MamTrgExames;
import br.gov.mec.aghu.model.MamTrgGerais;
import br.gov.mec.aghu.model.MamTrgMedicacoes;
import br.gov.mec.aghu.model.MamUnidAtendem;

public class MamTriagemVO implements Serializable {
	private static final long serialVersionUID = -4101227518458831214L;

	private Long trgSeq;
	private Integer pacCodigo;
	private String nomePaciente;
	private String queixaPrincipal;
	private Date dataQueixa;
	private Date horaQueixa;
	private Boolean indInternado;
	private Boolean indDataQueixaObr;
	private Boolean indHoraQueixaObr;
	private String codCor;
	private String descricaoGravidade;
	private String informacoesPaciente;
	private Integer seqOrigem;
	private MamOrigemPaciente mamOrigemPaciente;
	private Boolean indObrOrgPaciente;
	private Boolean houveContato;
	private String nomeContato;
	private String informacoesComplementares;
	private String alergias;
	private Short seqpAlergia;
	private Short unfSeq;
	private MamUnidAtendem mamUnidAtendem;
	private Integer seqHospitalInternado; 
	
	// Dados gerais
	private List<MamTrgGerais> listMamTrgGerais;
	// Exames
	private List<MamTrgExames> listMamTrgExames;
	// Medicações
	private List<MamTrgMedicacoes> listMamTrgMedicacoes;
	
	
	
	public MamTriagemVO() {
		super();
	}

	public enum Fields {
		TRG_SEQ("trgSeq"),
		NOME_PACIENTE("nomePaciente"),
		QUEIXA_PRINCIPAL("queixaPrincipal"),
		DATA_QUEIXA("dataQueixa"),
		HORA_QUEIXA("horaQueixa"),
		IND_INTERNADO("indInternado"),
		COD_COR("codCor"),
		DESCRICAO_GRAVIDADE("descricaoGravidade"),
		INFO_PACIENTE("informacoesPaciente"),
		SEQ_ORIGEM("seqOrigem"),
		IND_OBR_ORG_PACIENTE("indObrOrgPaciente"),
		HOUVE_CONTATO("houveContato"),
		NOME_CONTATO("nomeContato"),
		INFO_COMPLEMENTARES("informacoesComplementares"),
		ALERGIAS("alergias"),
		SEQP_ALERGIA("seqpAlergia"),
		UNF_SEQ("unfSeq"),
		UNID_ATENDEM("mamUnidAtendem");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getQueixaPrincipal() {
		return queixaPrincipal;
	}

	public void setQueixaPrincipal(String queixaPrincipal) {
		this.queixaPrincipal = queixaPrincipal;
	}

	public Date getDataQueixa() {
		return dataQueixa;
	}

	public void setDataQueixa(Date dataQueixa) {
		this.dataQueixa = dataQueixa;
	}

	public Date getHoraQueixa() {
		return horaQueixa;
	}

	public void setHoraQueixa(Date horaQueixa) {
		this.horaQueixa = horaQueixa;
	}

	public Boolean getIndInternado() {
		return indInternado;
	}

	public void setIndInternado(Boolean indInternado) {
		this.indInternado = indInternado;
	}

	public Boolean getIndDataQueixaObr() {
		return indDataQueixaObr;
	}

	public void setIndDataQueixaObr(Boolean indDataQueixaObr) {
		this.indDataQueixaObr = indDataQueixaObr;
	}

	public Boolean getIndHoraQueixaObr() {
		return indHoraQueixaObr;
	}

	public void setIndHoraQueixaObr(Boolean indHoraQueixaObr) {
		this.indHoraQueixaObr = indHoraQueixaObr;
	}

	public String getCodCor() {
		return codCor;
	}

	public void setCodCor(String codCor) {
		this.codCor = codCor;
	}

	public String getDescricaoGravidade() {
		return descricaoGravidade;
	}

	public void setDescricaoGravidade(String descricaoGravidade) {
		this.descricaoGravidade = descricaoGravidade;
	}

	public String getInformacoesPaciente() {
		return informacoesPaciente;
	}

	public void setInformacoesPaciente(String informacoesPaciente) {
		this.informacoesPaciente = informacoesPaciente;
	}

	public MamOrigemPaciente getMamOrigemPaciente() {
		return mamOrigemPaciente;
	}

	public void setMamOrigemPaciente(MamOrigemPaciente mamOrigemPaciente) {
		this.mamOrigemPaciente = mamOrigemPaciente;
	}

	public Boolean getIndObrOrgPaciente() {
		return indObrOrgPaciente;
	}

	public void setIndObrOrgPaciente(Boolean indObrOrgPaciente) {
		this.indObrOrgPaciente = indObrOrgPaciente;
	}

	public Boolean getHouveContato() {
		return houveContato;
	}

	public void setHouveContato(Boolean houveContato) {
		this.houveContato = houveContato;
	}

	public String getNomeContato() {
		return nomeContato;
	}

	public void setNomeContato(String nomeContato) {
		this.nomeContato = nomeContato;
	}

	public String getInformacoesComplementares() {
		return informacoesComplementares;
	}

	public void setInformacoesComplementares(String informacoesComplementares) {
		this.informacoesComplementares = informacoesComplementares;
	}

	public Integer getSeqOrigem() {
		return seqOrigem;
	}

	public void setSeqOrigem(Integer seqOrigem) {
		this.seqOrigem = seqOrigem;
	}

	public String getAlergias() {
		return alergias;
	}

	public void setAlergias(String alergias) {
		this.alergias = alergias;
	}

	public Short getSeqpAlergia() {
		return seqpAlergia;
	}

	public void setSeqpAlergia(Short seqpAlergia) {
		this.seqpAlergia = seqpAlergia;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public MamUnidAtendem getMamUnidAtendem() {
		return mamUnidAtendem;
	}

	public void setMamUnidAtendem(MamUnidAtendem mamUnidAtendem) {
		this.mamUnidAtendem = mamUnidAtendem;
	}

	public List<MamTrgGerais> getListMamTrgGerais() {
		return listMamTrgGerais;
	}

	public void setListMamTrgGerais(List<MamTrgGerais> listMamTrgGerais) {
		this.listMamTrgGerais = listMamTrgGerais;
	}

	public List<MamTrgExames> getListMamTrgExames() {
		return listMamTrgExames;
	}

	public void setListMamTrgExames(List<MamTrgExames> listMamTrgExames) {
		this.listMamTrgExames = listMamTrgExames;
	}

	public List<MamTrgMedicacoes> getListMamTrgMedicacoes() {
		return listMamTrgMedicacoes;
	}

	public void setListMamTrgMedicacoes(List<MamTrgMedicacoes> listMamTrgMedicacoes) {
		this.listMamTrgMedicacoes = listMamTrgMedicacoes;
	}
	
	public Integer getSeqHospitalInternado() {
		return seqHospitalInternado;
	}

	public void setSeqHospitalInternado(Integer seqHospitalInternado) {
		this.seqHospitalInternado = seqHospitalInternado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((seqHospitalInternado == null) ? 0 : seqHospitalInternado
						.hashCode());
		result = prime * result + ((trgSeq == null) ? 0 : trgSeq.hashCode());
		result = prime * result + ((unfSeq == null) ? 0 : unfSeq.hashCode());
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
		if (!(obj instanceof MamTriagemVO)) {
			return false;
		}
		MamTriagemVO other = (MamTriagemVO) obj;
		if (seqHospitalInternado == null) {
			if (other.seqHospitalInternado != null) {
				return false;
			}
		} else if (!seqHospitalInternado.equals(other.seqHospitalInternado)) {
			return false;
		}
		if (trgSeq == null) {
			if (other.trgSeq != null) {
				return false;
			}
		} else if (!trgSeq.equals(other.trgSeq)) {
			return false;
		}
		if (unfSeq == null) {
			if (other.unfSeq != null) {
				return false;
			}
		} else if (!unfSeq.equals(other.unfSeq)) {
			return false;
		}
		return true;
	}

}
