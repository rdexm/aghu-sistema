package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

public class RelatorioProdutividadePorAnestesistaVO implements Serializable {
	
	private static final long serialVersionUID = 5248602039147696816L;

	private String descFuncaoProfissional; 						//5
	private String descSerCodigoMatricula;						//6
	private String nomeFuncionario; 							//7
	private Long qtdCirurgia;								//8
	private Long qtdExecutado;								//9
	private String qtdHora;										//10
	private Integer qtdSupervisionado;							//11
	private List<LinhaReportVO> tipoAnestesia;					//12 13 14  C3.TAN_SEQ_2 C3.ANESTESIA_2 C3.QTD_ANC_2
	private List<LinhaReportVO> caracteristicaAnestesia;		//15 16 C4.NATUREZA_3 C4.QTDE_CAR_3
	private List<LinhaReportVO> especialidadeAnestesia;			//17 18 C5.SIGLA_4 || C5.ESPECIALIDADE_4 C5.QTD_ESP_4
	private Long qtdAtoAnestesico;							//19 23
	private Long qtdAtoAnestesicoExecutado;					//20
	private String qtdHoraAnestesia;							//21 26
	private Integer qtdHoraAnestesiaSupervisionado;				//22
	private Integer qtdProfAtoAnestesico;						//24 27 Count da lista?????
	private BigDecimal mediaAtoAnestesico;							//25
	private String mediaHoraAnestesia;							//28	
	private List<LinhaReportVO> totalTipoAnestesia;				//29 30 31 C7.TAN_SEQ_21 C7.ANESTESIA_21 C7.QTD_ANC_21
	private List<LinhaReportVO> totalCaracteristicaAnestesia;	//32 33 C8.NATUREZA_31 C8.QTD_CAR_31
	private List<LinhaReportVO> totalEspecialidadeAnestesia;	//34 35 36	C9.SIGLA_41 C9.ESPECIALIDADE_41 C9.QTD_ESP_41
	
	private Short pucVinCodigo;
	private Integer pucMatricula;
	private Short pucUnfSeq;
	private Date dataInicial;
	private Date dataFinal;
	private DominioFuncaoProfissional dominioFuncaoProfissional;

	public RelatorioProdutividadePorAnestesistaVO() {
		
	}
	
	public enum Fields {

		DESCRICAO_UNIDADE("descricaoUnidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	//Getters and Setters

	public String getDescFuncaoProfissional() {
		return descFuncaoProfissional;
	}

	public void setDescFuncaoProfissional(String descFuncaoProfissional) {
		this.descFuncaoProfissional = descFuncaoProfissional;
	}

	public String getDescSerCodigoMatricula() {
		return descSerCodigoMatricula;
	}

	public void setDescSerCodigoMatricula(String descSerCodigoMatricula) {
		this.descSerCodigoMatricula = descSerCodigoMatricula;
	}

	public String getNomeFuncionario() {
		return nomeFuncionario;
	}

	public void setNomeFuncionario(String nomeFuncionario) {
		this.nomeFuncionario = nomeFuncionario;
	}

	public Long getQtdCirurgia() {
		return qtdCirurgia;
	}

	public void setQtdCirurgia(Long qtdCirurgia) {
		this.qtdCirurgia = qtdCirurgia;
	}

	public Long getQtdExecutado() {
		return qtdExecutado;
	}

	public void setQtdExecutado(Long qtdExecutado) {
		this.qtdExecutado = qtdExecutado;
	}

	public String getQtdHora() {
		return qtdHora;
	}

	public void setQtdHora(String qtdHora) {
		this.qtdHora = qtdHora;
	}

	public Integer getQtdSupervisionado() {
		return qtdSupervisionado;
	}

	public void setQtdSupervisionado(Integer qtdSupervisionado) {
		this.qtdSupervisionado = qtdSupervisionado;
	}

	public List<LinhaReportVO> getTipoAnestesia() {
		return tipoAnestesia;
	}

	public void setTipoAnestesia(List<LinhaReportVO> tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}

	public List<LinhaReportVO> getCaracteristicaAnestesia() {
		return caracteristicaAnestesia;
	}

	public void setCaracteristicaAnestesia(
			List<LinhaReportVO> caracteristicaAnestesia) {
		this.caracteristicaAnestesia = caracteristicaAnestesia;
	}

	public List<LinhaReportVO> getEspecialidadeAnestesia() {
		return especialidadeAnestesia;
	}

	public void setEspecialidadeAnestesia(List<LinhaReportVO> especialidadeAnestesia) {
		this.especialidadeAnestesia = especialidadeAnestesia;
	}

	public Long getQtdAtoAnestesico() {
		return qtdAtoAnestesico;
	}

	public void setQtdAtoAnestesico(Long qtdAtoAnestesico) {
		this.qtdAtoAnestesico = qtdAtoAnestesico;
	}

	public Long getQtdAtoAnestesicoExecutado() {
		return qtdAtoAnestesicoExecutado;
	}

	public void setQtdAtoAnestesicoExecutado(Long qtdAtoAnestesicoExecutado) {
		this.qtdAtoAnestesicoExecutado = qtdAtoAnestesicoExecutado;
	}

	public String getQtdHoraAnestesia() {
		return qtdHoraAnestesia;
	}

	public void setQtdHoraAnestesia(String qtdHoraAnestesia) {
		this.qtdHoraAnestesia = qtdHoraAnestesia;
	}

	public Integer getQtdHoraAnestesiaSupervisionado() {
		return qtdHoraAnestesiaSupervisionado;
	}

	public void setQtdHoraAnestesiaSupervisionado(
			Integer qtdHoraAnestesiaSupervisionado) {
		this.qtdHoraAnestesiaSupervisionado = qtdHoraAnestesiaSupervisionado;
	}

	public Integer getQtdProfAtoAnestesico() {
		return qtdProfAtoAnestesico;
	}

	public void setQtdProfAtoAnestesico(Integer qtdProfAtoAnestesico) {
		this.qtdProfAtoAnestesico = qtdProfAtoAnestesico;
	}

	public BigDecimal getMediaAtoAnestesico() {
		return mediaAtoAnestesico;
	}

	public void setMediaAtoAnestesico(BigDecimal mediaAtoAnestesico) {
		this.mediaAtoAnestesico = mediaAtoAnestesico;
	}

	public List<LinhaReportVO> getTotalTipoAnestesia() {
		return totalTipoAnestesia;
	}

	public void setTotalTipoAnestesia(List<LinhaReportVO> totalTipoAnestesia) {
		this.totalTipoAnestesia = totalTipoAnestesia;
	}

	public List<LinhaReportVO> getTotalCaracteristicaAnestesia() {
		return totalCaracteristicaAnestesia;
	}

	public void setTotalCaracteristicaAnestesia(
			List<LinhaReportVO> totalCaracteristicaAnestesia) {
		this.totalCaracteristicaAnestesia = totalCaracteristicaAnestesia;
	}

	public List<LinhaReportVO> getTotalEspecialidadeAnestesia() {
		return totalEspecialidadeAnestesia;
	}

	public void setTotalEspecialidadeAnestesia(
			List<LinhaReportVO> totalEspecialidadeAnestesia) {
		this.totalEspecialidadeAnestesia = totalEspecialidadeAnestesia;
	}

	public String getMediaHoraAnestesia() {
		return mediaHoraAnestesia;
	}

	public void setMediaHoraAnestesia(String mediaHoraAnestesia) {
		this.mediaHoraAnestesia = mediaHoraAnestesia;
	}

	public void setPucVinCodigo(Short pucVinCodigo) {
		this.pucVinCodigo = pucVinCodigo;
	}

	public Short getPucVinCodigo() {
		return pucVinCodigo;
	}

	public void setPucMatricula(Integer pucMatricula) {
		this.pucMatricula = pucMatricula;
	}

	public Integer getPucMatricula() {
		return pucMatricula;
	}

	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}

	public Short getPucUnfSeq() {
		return pucUnfSeq;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDominioFuncaoProfissional(DominioFuncaoProfissional dominioFuncaoProfissional) {
		this.dominioFuncaoProfissional = dominioFuncaoProfissional;
	}

	public DominioFuncaoProfissional getDominioFuncaoProfissional() {
		return dominioFuncaoProfissional;
	}
}