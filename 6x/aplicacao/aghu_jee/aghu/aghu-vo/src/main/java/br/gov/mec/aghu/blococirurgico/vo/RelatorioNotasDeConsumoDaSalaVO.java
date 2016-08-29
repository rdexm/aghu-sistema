package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class RelatorioNotasDeConsumoDaSalaVO implements Serializable {
	
	private static final long serialVersionUID = -962601384321968243L;

	private Short sciSeqp;							//7
	private String pacProntuario;					//9
	private String pacNome;							//10
	private String quarto; 							//12
	private String cnvCodigoDescricao;				//14
	private Short nroAgenda;						//16
	private String procedimento;	    			//18
	private Date dtCirurgia;
	private String descricaoAnestesia;				//21
	private String dtHrInicioCirurgia;				//25
	private String dtHrFimCirurgia;					//29
	private String cirurgiaoMpf;            		//39
	private String cirurgiaoMco;         			//41
	private String cirurgiaoMax;            		//43
	private String anestesistaAnp;          		//46
	private String anestesistaAnc;          		//48
	private String anestesistaAnr;          		//50
	private String enfermeiraEpf;           		//53
	private String enfermeiraEco;           		//55
	private String enfermeiraEax;           		//57
	private String circulanteCpf;           		//60
	private String circulanteCco;           		//62
	private String circulanteCax;           		//64
	private String instrumentInp;           		//67
	private String instrumentIno;           		//69
	private String instrumentInx;           		//71
	private String descricaoEquipamentoColuna1;		//77
	private String descricaoEquipamentoColuna2;		//77
	private String descricaoEquipamentoColuna3;		//77
	private String descricaoEquipamentoColuna4;		//77
	private String descricaoOrteseProteseColuna1;	//84
	private String descricaoOrteseProteseColuna2;	//84
	private String descricaoOrteseProteseColuna3;	//84
	private List<SubRelatorioNotasDeConsumoDaSalaExamesVO> subRelatorioExames;
	private List<SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO> subRelatorioSangueUtilizado;
	private List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> subRelatorioEquipamentos;
	private List<SubRelatorioNotasDeConsumoDaSalaMateriaisVO> subRelatorioMateriais;
	
	//História #28724
	private String dtHrEntradaSala;					//1
	private String dtHrSaidaSala;					//1
	private String dtHrInicioAnest;					//2
	private String dtHrFimAnest;					//2
	private String indUtilO2;						//3
	private Boolean aplicaListaCirurgiaSegura;		//6
	private String motivoCancelamento;				//7
	private String destinoPaciente;					//8
	private String pacProntuarioFormatado;			
	private String indPrc;
	
	private String atbProf;		
	private String dtHrAtbProf;
	
	public RelatorioNotasDeConsumoDaSalaVO() {
		super();
	}
	
	public enum Fields {

		DT_HR_INICIO("dtHrInicioCirurgia"),
		SCI_SEQP("sciSeqp");

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
	
	public Short getSciSeqp() {
		return sciSeqp;
	}

	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}

	public String getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(String pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public String getQuarto() {
		return quarto;
	}

	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}

	public String getCnvCodigoDescricao() {
		return cnvCodigoDescricao;
	}

	public void setCnvCodigoDescricao(String cnvCodigoDescricao) {
		this.cnvCodigoDescricao = cnvCodigoDescricao;
	}

	public Short getNroAgenda() {
		return nroAgenda;
	}

	public void setNroAgenda(Short nroAgenda) {
		this.nroAgenda = nroAgenda;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public Date getDtCirurgia() {
		return dtCirurgia;
	}

	public void setDtCirurgia(Date dtCirurgia) {
		this.dtCirurgia = dtCirurgia;
	}
	
	public String getDescricaoAnestesia() {
		return descricaoAnestesia;
	}

	public void setDescricaoAnestesia(String descricaoAnestesia) {
		this.descricaoAnestesia = descricaoAnestesia;
	}

	public String getDtHrInicioCirurgia() {
		return dtHrInicioCirurgia;
	}

	public void setDtHrInicioCirurgia(String dtHrInicioCirurgia) {
		this.dtHrInicioCirurgia = dtHrInicioCirurgia;
	}

	public String getDtHrFimCirurgia() {
		return dtHrFimCirurgia;
	}

	public void setDtHrFimCirurgia(String dtHrFimCirurgia) {
		this.dtHrFimCirurgia = dtHrFimCirurgia;
	}

	public String getCirurgiaoMpf() {
		return cirurgiaoMpf;
	}

	public void setCirurgiaoMpf(String cirurgiaoMpf) {
		this.cirurgiaoMpf = cirurgiaoMpf;
	}

	public String getCirurgiaoMco() {
		return cirurgiaoMco;
	}

	public void setCirurgiaoMco(String cirurgiaoMco) {
		this.cirurgiaoMco = cirurgiaoMco;
	}

	public String getCirurgiaoMax() {
		return cirurgiaoMax;
	}

	public void setCirurgiaoMax(String cirurgiaoMax) {
		this.cirurgiaoMax = cirurgiaoMax;
	}

	public String getAnestesistaAnp() {
		return anestesistaAnp;
	}

	public void setAnestesistaAnp(String anestesistaAnp) {
		this.anestesistaAnp = anestesistaAnp;
	}

	public String getAnestesistaAnc() {
		return anestesistaAnc;
	}

	public void setAnestesistaAnc(String anestesistaAnc) {
		this.anestesistaAnc = anestesistaAnc;
	}

	public String getAnestesistaAnr() {
		return anestesistaAnr;
	}

	public void setAnestesistaAnr(String anestesistaAnr) {
		this.anestesistaAnr = anestesistaAnr;
	}

	public String getEnfermeiraEpf() {
		return enfermeiraEpf;
	}

	public void setEnfermeiraEpf(String enfermeiraEpf) {
		this.enfermeiraEpf = enfermeiraEpf;
	}

	public String getEnfermeiraEco() {
		return enfermeiraEco;
	}

	public void setEnfermeiraEco(String enfermeiraEco) {
		this.enfermeiraEco = enfermeiraEco;
	}

	public String getEnfermeiraEax() {
		return enfermeiraEax;
	}

	public void setEnfermeiraEax(String enfermeiraEax) {
		this.enfermeiraEax = enfermeiraEax;
	}

	public String getCirculanteCpf() {
		return circulanteCpf;
	}

	public void setCirculanteCpf(String circulanteCpf) {
		this.circulanteCpf = circulanteCpf;
	}

	public String getCirculanteCco() {
		return circulanteCco;
	}

	public void setCirculanteCco(String circulanteCco) {
		this.circulanteCco = circulanteCco;
	}

	public String getCirculanteCax() {
		return circulanteCax;
	}

	public void setCirculanteCax(String circulanteCax) {
		this.circulanteCax = circulanteCax;
	}

	public String getInstrumentInp() {
		return instrumentInp;
	}

	public void setInstrumentInp(String instrumentInp) {
		this.instrumentInp = instrumentInp;
	}

	public String getInstrumentIno() {
		return instrumentIno;
	}

	public void setInstrumentIno(String instrumentIno) {
		this.instrumentIno = instrumentIno;
	}

	public String getInstrumentInx() {
		return instrumentInx;
	}

	public void setInstrumentInx(String instrumentInx) {
		this.instrumentInx = instrumentInx;
	}

	public String getDescricaoEquipamentoColuna1() {
		return descricaoEquipamentoColuna1;
	}

	public void setDescricaoEquipamentoColuna1(String descricaoEquipamentoColuna1) {
		this.descricaoEquipamentoColuna1 = descricaoEquipamentoColuna1;
	}

	public String getDescricaoEquipamentoColuna2() {
		return descricaoEquipamentoColuna2;
	}

	public void setDescricaoEquipamentoColuna2(String descricaoEquipamentoColuna2) {
		this.descricaoEquipamentoColuna2 = descricaoEquipamentoColuna2;
	}

	public String getDescricaoEquipamentoColuna3() {
		return descricaoEquipamentoColuna3;
	}

	public void setDescricaoEquipamentoColuna3(String descricaoEquipamentoColuna3) {
		this.descricaoEquipamentoColuna3 = descricaoEquipamentoColuna3;
	}

	public String getDescricaoEquipamentoColuna4() {
		return descricaoEquipamentoColuna4;
	}

	public void setDescricaoEquipamentoColuna4(String descricaoEquipamentoColuna4) {
		this.descricaoEquipamentoColuna4 = descricaoEquipamentoColuna4;
	}

	public String getDescricaoOrteseProteseColuna1() {
		return descricaoOrteseProteseColuna1;
	}

	public void setDescricaoOrteseProteseColuna1(
			String descricaoOrteseProteseColuna1) {
		this.descricaoOrteseProteseColuna1 = descricaoOrteseProteseColuna1;
	}

	public String getDescricaoOrteseProteseColuna2() {
		return descricaoOrteseProteseColuna2;
	}

	public void setDescricaoOrteseProteseColuna2(
			String descricaoOrteseProteseColuna2) {
		this.descricaoOrteseProteseColuna2 = descricaoOrteseProteseColuna2;
	}

	public String getDescricaoOrteseProteseColuna3() {
		return descricaoOrteseProteseColuna3;
	}

	public void setDescricaoOrteseProteseColuna3(
			String descricaoOrteseProteseColuna3) {
		this.descricaoOrteseProteseColuna3 = descricaoOrteseProteseColuna3;
	}

	public List<SubRelatorioNotasDeConsumoDaSalaExamesVO> getSubRelatorioExames() {
		return subRelatorioExames;
	}

	public void setSubRelatorioExames(
			List<SubRelatorioNotasDeConsumoDaSalaExamesVO> subRelatorioExames) {
		this.subRelatorioExames = subRelatorioExames;
	}

	public List<SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO> getSubRelatorioSangueUtilizado() {
		return subRelatorioSangueUtilizado;
	}

	public void setSubRelatorioSangueUtilizado(
			List<SubRelatorioNotasDeConsumoDaSalaSangueUtilizadoVO> subRelatorioSangueUtilizado) {
		this.subRelatorioSangueUtilizado = subRelatorioSangueUtilizado;
	}

	public List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> getSubRelatorioEquipamentos() {
		return subRelatorioEquipamentos;
	}

	public void setSubRelatorioEquipamentos(
			List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> subRelatorioEquipamentos) {
		this.subRelatorioEquipamentos = subRelatorioEquipamentos;
	}

	public List<SubRelatorioNotasDeConsumoDaSalaMateriaisVO> getSubRelatorioMateriais() {
		return subRelatorioMateriais;
	}

	public void setSubRelatorioMateriais(
			List<SubRelatorioNotasDeConsumoDaSalaMateriaisVO> subRelatorioMateriais) {
		this.subRelatorioMateriais = subRelatorioMateriais;
	}

	public String getDtHrEntradaSala() {
		return dtHrEntradaSala;
	}

	public void setDtHrEntradaSala(String dtHrEntradaSala) {
		this.dtHrEntradaSala = dtHrEntradaSala;
	}

	public String getDtHrSaidaSala() {
		return dtHrSaidaSala;
	}

	public void setDtHrSaidaSala(String dtHrSaidaSala) {
		this.dtHrSaidaSala = dtHrSaidaSala;
	}

	public String getDtHrInicioAnest() {
		return dtHrInicioAnest;
	}

	public void setDtHrInicioAnest(String dtHrInicioAnest) {
		this.dtHrInicioAnest = dtHrInicioAnest;
	}

	public String getDtHrFimAnest() {
		return dtHrFimAnest;
	}

	public void setDtHrFimAnest(String dtHrFimAnest) {
		this.dtHrFimAnest = dtHrFimAnest;
	}

	public String getIndUtilO2() {
		return indUtilO2;
	}

	public void setIndUtilO2(String indUtilO2) {
		this.indUtilO2 = indUtilO2;
	}

	public Boolean getAplicaListaCirurgiaSegura() {
		return aplicaListaCirurgiaSegura;
	}

	public void setAplicaListaCirurgiaSegura(Boolean aplicaListaCirurgiaSegura) {
		this.aplicaListaCirurgiaSegura = aplicaListaCirurgiaSegura;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public String getDestinoPaciente() {
		return destinoPaciente;
	}

	public void setDestinoPaciente(String destinoPaciente) {
		this.destinoPaciente = destinoPaciente;
	}

	public String getPacProntuarioFormatado() {
		return pacProntuarioFormatado;
	}

	public void setPacProntuarioFormatado(String pacProntuarioFormatado) {
		this.pacProntuarioFormatado = pacProntuarioFormatado;
	}

	public String getIndPrc() {
		return indPrc;
	}

	public void setIndPrc(String indPrc) {
		this.indPrc = indPrc;
	}

	public String getAtbProf() {
		return atbProf;
	}

	public void setAtbProf(String atbProf) {
		this.atbProf = atbProf;
	}

	public String getDtHrAtbProf() {
		return dtHrAtbProf;
	}

	public void setDtHrAtbProf(String dtHrAtbProf) {
		this.dtHrAtbProf = dtHrAtbProf;
	}
	
	
}