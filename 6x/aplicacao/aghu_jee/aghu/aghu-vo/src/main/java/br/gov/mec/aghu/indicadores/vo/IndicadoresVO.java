package br.gov.mec.aghu.indicadores.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AinIndicadoresHospitalares;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class IndicadoresVO {

	// tabela com movimentos de bloqueio de leitos
	private Integer lst;
	private Integer ind = 0;
	private Integer diasAcum = 0;

	private Map<Integer, LeitosBloqueadosIndicadoresVO> tabBlq = new HashMap<Integer, LeitosBloqueadosIndicadoresVO>();

	// tabela com movimentos de leitos desativados
	private Integer lstDesat;
	private Integer indDesat = 0;
	private Integer diasDesatAcum = 0;

	private Map<Short, LeitosDesativadosIndicadoresVO> tabDesat = new HashMap<Short, LeitosDesativadosIndicadoresVO>();

	// tabela de capacidade/bloqueios de unidades
	private Integer indUnid = 0;
	private Integer lstUnid;
	private Integer indUnidPriv = 0;
	private Integer lstUnidPriv = 0;
	private Integer unid = 0;

	private Integer indEsp = 0;
	private Integer lstEsp;

	private Map<Short, CapacidadeEspecialidadeIndicadoresVO> tabEsp = new HashMap<Short, CapacidadeEspecialidadeIndicadoresVO>();

	private Map<Short, UnidadeFuncionalIndicadoresVO> tabUnid = new HashMap<Short, UnidadeFuncionalIndicadoresVO>();

	private Map<Short, LeitoIndicadoresVO> tabUnidPriv = new HashMap<Short, LeitoIndicadoresVO>();

	private Map<Integer, ClinicaIndicadoresVO> tabClinicas = new HashMap<Integer, ClinicaIndicadoresVO>();

	private Map<Integer, AinIndicadoresHospitalares> tabIndicadores = new HashMap<Integer, AinIndicadoresHospitalares>();

	private Integer indTab = 0;
	private Integer lstTab = 0;
	private Boolean achou = false;

	private Integer xintSeq = 0;
	private Integer xatuSeq;
	private String xtamCodigo;
	private Integer xtmiSeq;
	private Short xunfSeq;
	private String xltoId;
	private Short xespSeq;
	private Short xespSeqProx;
	private Short xunfSeqProx;
	private Short xserVinCodigo;
	private Integer xserMatricula;
	private Date xdthrLancamento;
	private Date xdthrFinal;
	private Integer xtmiSeqProx;
	private Integer xcctCodigo;
	private Integer xclcCodigo;
	private Integer xclcCodigoLto;
	private Integer xclcCodigoPac;
	private Integer xphiSeq;
	private Integer xgphSeq;
	private Integer xcddCodigo;
	private Integer xbclBaiCodigo;
	private Integer xdstCodigo;
	private Boolean xindConsClinLto;
	private DominioSimNao xindConsClinQrt;
	private DominioSimNao xindConsClinUnf;
	private DominioSimNao xindExclusivInfeccao;
	private Short xcnvCodigo = 0;
	private Short yoevSeq;
	private Short xoevSeq;
	private Date xdtAlta;
	private Date ydtAlta;
	private Integer yintSeq;
	private Integer yatuSeq;
	private String ytamCodigo;
	private Integer ytmiSeq;
	private Short yunfSeq;
	private String yltoId;
	private Short yespSeq;
	private Integer zespSeq;
	private Short yespSeqProx;
	private Short yunfSeqProx;
	private Short yserVinCodigo;
	private Integer yserMatricula;
	private Date ydthrLancamento;
	private Date ydthrFinal;
	private Integer ytmiSeqProx;
	private Integer ycctCodigo;
	private Integer yclcCodigo;
	private Integer yclcCodigoLto;
	private Integer yclcCodigoPac;
	private Integer yphiSeq;
	private Integer ygphSeq;
	private Integer ycddCodigo;
	private Integer ybclBaiCodigo;
	private Integer ydstCodigo;
	private Boolean yindConsClinLto;
	private DominioSimNao yindConsClinQrt;
	private DominioSimNao yindConsClinUnf;
	private DominioSimNao yindExclusivInfeccao;
	private Short ycnvCodigo = 0;

	private Integer xprontuario;
	private Integer yprontuario;
	private String xltoPrivativo;
	private String yltoPrivativo;
	private Boolean naoFim = true;
	private Boolean naoAlta = true;
	private Boolean naoQuebra = true;
	private String xdata;
	private String xsigla;
	private String ysigla;
	private String ydata;
	private Date xdatai;
	private Date xdataf;
	private BigDecimal xpacDia;
	private BigDecimal xpacDiaRefl;
	private Integer xltoDia;
	private Integer xadm;
	private Integer xasat;
	private Short xoutClinE;
	private Short xoutClinS;
	private Short xoutUnidE;
	private Short xoutUnidS;
	private Short xoutEspE;
	private Short xoutEspS;
	private Short xoutProfE;
	private Short xoutProfS;
	private Integer xalt;
	private Short xobtC;
	private Integer xobtD;
	private Short xsdo;
	private Integer xpacMesAnt;
	private Integer xblq;
	private Integer xcapcReflClin;
	private Integer xsaidas;
	private Integer xentradas;
	private String vOutClin = "N"; // Sim, Não
	private String vOrgmAsat = "N"; // Sim, Não
	private String vOrgmBlc = "N"; // Sim, Não
	private String xOutClin = "N"; // Sim, Não
	private String xtipo;
	private String ytipo;
	private BigDecimal vCapacidade;
	private Integer vLtoDia;
	private Integer vOcupacao;
	private Integer vCommit = 0;
	
	// 01; inicializa_tab; valores; nome_lista
	private List<String> valoresLog = new ArrayList<String>();
	private Integer countLog;

	public IndicadoresVO() {
		super();
		this.ind = 0;
		this.diasAcum = 0;
		this.indDesat = 0;
		this.diasDesatAcum = 0;
		this.indUnid = 0;
		this.lstUnid = 0;
		this.lstUnidPriv = 0;
		this.indUnidPriv = 0;
		this.unid = 0;
		this.indEsp = 0;
		this.indTab = 0;
		this.lstTab = 0;
		this.xcnvCodigo = 0;
		this.ycnvCodigo = 0;
		this.xintSeq = 0;
		this.vCommit = 0;

		this.vOutClin = "N";
		this.vOrgmAsat = "N";
		this.vOrgmBlc = "N";
		this.xOutClin = "N";

		this.achou = false;
		this.naoFim = true;
		this.naoAlta = true;
		this.naoQuebra = true;
	}

	public Integer getLst() {
		return lst;
	}

	public void setLst(Integer lst) {
		this.lst = lst;
	}

	public Integer getInd() {
		return ind;
	}

	public void setInd(Integer ind) {
		this.ind = ind;
	}

	public Integer getDiasAcum() {
		return diasAcum;
	}

	public void setDiasAcum(Integer diasAcum) {
		this.diasAcum = diasAcum;
	}

	public Integer getLstDesat() {
		return lstDesat;
	}

	public void setLstDesat(Integer lstDesat) {
		this.lstDesat = lstDesat;
	}

	public Integer getIndDesat() {
		return indDesat;
	}

	public void setIndDesat(Integer indDesat) {
		this.indDesat = indDesat;
	}

	public Integer getDiasDesatAcum() {
		return diasDesatAcum;
	}

	public void setDiasDesatAcum(Integer diasDesatAcum) {
		this.diasDesatAcum = diasDesatAcum;
	}

	public Integer getIndUnid() {
		return indUnid;
	}

	public void setIndUnid(Integer indUnid) {
		this.indUnid = indUnid;
	}

	public Integer getLstUnid() {
		return lstUnid;
	}

	public void setLstUnid(Integer lstUnid) {
		this.lstUnid = lstUnid;
	}

	public Integer getIndUnidPriv() {
		return indUnidPriv;
	}

	public void setIndUnidPriv(Integer indUnidPriv) {
		this.indUnidPriv = indUnidPriv;
	}

	public Integer getLstUnidPriv() {
		return lstUnidPriv;
	}

	public void setLstUnidPriv(Integer lstUnidPriv) {
		this.lstUnidPriv = lstUnidPriv;
	}

	public Integer getUnid() {
		return unid;
	}

	public void setUnid(Integer unid) {
		this.unid = unid;
	}

	public Integer getIndEsp() {
		return indEsp;
	}

	public void setIndEsp(Integer indEsp) {
		this.indEsp = indEsp;
	}

	public void incrIndEsp() {
		if (this.indEsp == null) {
			this.indEsp = 0;
		}
		this.indEsp++;
	}

	public Integer getLstEsp() {
		return lstEsp;
	}

	public void setLstEsp(Integer lstEsp) {
		this.lstEsp = lstEsp;
	}

	public Integer getIndTab() {
		return indTab;
	}

	public void setIndTab(Integer indTab) {
		this.indTab = indTab;
	}

	public void incrIndTab() {
		if (this.indTab == null) {
			this.indTab = 0;
		}
		this.indTab++;
	}

	public Integer getLstTab() {
		return lstTab;
	}

	public void setLstTab(Integer lstTab) {
		this.lstTab = lstTab;
	}

	public Boolean getAchou() {
		return achou;
	}

	public void setAchou(Boolean achou) {
		this.achou = achou;
	}

	public Integer getXintSeq() {
		return xintSeq;
	}

	public void setXintSeq(Integer xintSeq) {
		this.xintSeq = xintSeq;
	}

	public Integer getXatuSeq() {
		return xatuSeq;
	}

	public void setXatuSeq(Integer xatuSeq) {
		this.xatuSeq = xatuSeq;
	}

	public String getXtamCodigo() {
		return xtamCodigo;
	}

	public void setXtamCodigo(String xtamCodigo) {
		this.xtamCodigo = xtamCodigo;
	}

	public Integer getXtmiSeq() {
		return xtmiSeq;
	}

	public void setXtmiSeq(Integer xtmiSeq) {
		this.xtmiSeq = xtmiSeq;
	}

	public Short getXunfSeq() {
		return xunfSeq;
	}

	public void setXunfSeq(Short xunfSeq) {
		this.xunfSeq = xunfSeq;
	}

	public String getXltoId() {
		return xltoId;
	}

	public void setXltoId(String xltoId) {
		this.xltoId = xltoId;
	}

	public Short getXespSeq() {
		return xespSeq;
	}

	public void setXespSeq(Short xespSeq) {
		this.xespSeq = xespSeq;
	}

	public Short getXespSeqProx() {
		return xespSeqProx;
	}

	public void setXespSeqProx(Short xespSeqProx) {
		this.xespSeqProx = xespSeqProx;
	}

	public Short getXunfSeqProx() {
		return xunfSeqProx;
	}

	public void setXunfSeqProx(Short xunfSeqProx) {
		this.xunfSeqProx = xunfSeqProx;
	}

	public Short getXserVinCodigo() {
		return xserVinCodigo;
	}

	public void setXserVinCodigo(Short xserVinCodigo) {
		this.xserVinCodigo = xserVinCodigo;
	}

	public Integer getXserMatricula() {
		return xserMatricula;
	}

	public void setXserMatricula(Integer xserMatricula) {
		this.xserMatricula = xserMatricula;
	}

	public Date getXdthrLancamento() {
		return xdthrLancamento;
	}

	public void setXdthrLancamento(Date xdthrLancamento) {
		this.xdthrLancamento = xdthrLancamento;
	}

	public Date getXdthrFinal() {
		return xdthrFinal;
	}

	public void setXdthrFinal(Date xdthrFinal) {
		this.xdthrFinal = xdthrFinal;
	}

	public Integer getXtmiSeqProx() {
		return xtmiSeqProx;
	}

	public void setXtmiSeqProx(Integer xtmiSeqProx) {
		this.xtmiSeqProx = xtmiSeqProx;
	}

	public Integer getXcctCodigo() {
		return xcctCodigo;
	}

	public void setXcctCodigo(Integer xcctCodigo) {
		this.xcctCodigo = xcctCodigo;
	}

	public Integer getXclcCodigo() {
		return xclcCodigo;
	}

	public void setXclcCodigo(Integer xclcCodigo) {
		this.xclcCodigo = xclcCodigo;
	}

	public Integer getXclcCodigoLto() {
		return xclcCodigoLto;
	}

	public void setXclcCodigoLto(Integer xclcCodigoLto) {
		this.xclcCodigoLto = xclcCodigoLto;
	}

	public Integer getXclcCodigoPac() {
		return xclcCodigoPac;
	}

	public void setXclcCodigoPac(Integer xclcCodigoPac) {
		this.xclcCodigoPac = xclcCodigoPac;
	}

	public Integer getXphiSeq() {
		return xphiSeq;
	}

	public void setXphiSeq(Integer xphiSeq) {
		this.xphiSeq = xphiSeq;
	}

	public Integer getXgphSeq() {
		return xgphSeq;
	}

	public void setXgphSeq(Integer xgphSeq) {
		this.xgphSeq = xgphSeq;
	}

	public Integer getXcddCodigo() {
		return xcddCodigo;
	}

	public void setXcddCodigo(Integer xcddCodigo) {
		this.xcddCodigo = xcddCodigo;
	}

	public Integer getXbclBaiCodigo() {
		return xbclBaiCodigo;
	}

	public void setXbclBaiCodigo(Integer xbclBaiCodigo) {
		this.xbclBaiCodigo = xbclBaiCodigo;
	}

	public Integer getXdstCodigo() {
		return xdstCodigo;
	}

	public void setXdstCodigo(Integer xdstCodigo) {
		this.xdstCodigo = xdstCodigo;
	}

	public Boolean getXindConsClinLto() {
		return xindConsClinLto;
	}

	public void setXindConsClinLto(Boolean xindConsClinLto) {
		this.xindConsClinLto = xindConsClinLto;
	}

	public DominioSimNao getXindConsClinQrt() {
		return xindConsClinQrt;
	}

	public void setXindConsClinQrt(DominioSimNao xindConsClinQrt) {
		this.xindConsClinQrt = xindConsClinQrt;
	}

	public DominioSimNao getXindConsClinUnf() {
		return xindConsClinUnf;
	}

	public void setXindConsClinUnf(DominioSimNao xindConsClinUnf) {
		this.xindConsClinUnf = xindConsClinUnf;
	}

	public DominioSimNao getXindExclusivInfeccao() {
		return xindExclusivInfeccao;
	}

	public void setXindExclusivInfeccao(DominioSimNao xindExclusivInfeccao) {
		this.xindExclusivInfeccao = xindExclusivInfeccao;
	}

	public Short getXcnvCodigo() {
		return xcnvCodigo;
	}

	public void setXcnvCodigo(Short xcnvCodigo) {
		this.xcnvCodigo = xcnvCodigo;
	}

	public Short getYoevSeq() {
		return yoevSeq;
	}

	public void setYoevSeq(Short yoevSeq) {
		this.yoevSeq = yoevSeq;
	}

	public Short getXoevSeq() {
		return xoevSeq;
	}

	public void setXoevSeq(Short xoevSeq) {
		this.xoevSeq = xoevSeq;
	}

	public Date getXdtAlta() {
		return xdtAlta;
	}

	public void setXdtAlta(Date xdtAlta) {
		this.xdtAlta = xdtAlta;
	}

	public Date getYdtAlta() {
		return ydtAlta;
	}

	public void setYdtAlta(Date ydtAlta) {
		this.ydtAlta = ydtAlta;
	}

	public Integer getYintSeq() {
		return yintSeq;
	}

	public void setYintSeq(Integer yintSeq) {
		this.yintSeq = yintSeq;
	}

	public Integer getYatuSeq() {
		return yatuSeq;
	}

	public void setYatuSeq(Integer yatuSeq) {
		this.yatuSeq = yatuSeq;
	}

	public String getYtamCodigo() {
		return ytamCodigo;
	}

	public void setYtamCodigo(String ytamCodigo) {
		this.ytamCodigo = ytamCodigo;
	}

	public Integer getYtmiSeq() {
		return ytmiSeq;
	}

	public void setYtmiSeq(Integer ytmiSeq) {
		this.ytmiSeq = ytmiSeq;
	}

	public Short getYunfSeq() {
		return yunfSeq;
	}

	public void setYunfSeq(Short yunfSeq) {
		this.yunfSeq = yunfSeq;
	}

	public String getYltoId() {
		return yltoId;
	}

	public void setYltoId(String yltoId) {
		this.yltoId = yltoId;
	}

	public Short getYespSeq() {
		return yespSeq;
	}

	public void setYespSeq(Short yespSeq) {
		this.yespSeq = yespSeq;
	}

	public Integer getZespSeq() {
		return zespSeq;
	}

	public void setZespSeq(Integer zespSeq) {
		this.zespSeq = zespSeq;
	}

	public Short getYespSeqProx() {
		return yespSeqProx;
	}

	public void setYespSeqProx(Short yespSeqProx) {
		this.yespSeqProx = yespSeqProx;
	}

	public Short getYunfSeqProx() {
		return yunfSeqProx;
	}

	public void setYunfSeqProx(Short yunfSeqProx) {
		this.yunfSeqProx = yunfSeqProx;
	}

	public Short getYserVinCodigo() {
		return yserVinCodigo;
	}

	public void setYserVinCodigo(Short yserVinCodigo) {
		this.yserVinCodigo = yserVinCodigo;
	}

	public Integer getYserMatricula() {
		return yserMatricula;
	}

	public void setYserMatricula(Integer yserMatricula) {
		this.yserMatricula = yserMatricula;
	}

	public Date getYdthrLancamento() {
		return ydthrLancamento;
	}

	public void setYdthrLancamento(Date ydthrLancamento) {
		this.ydthrLancamento = ydthrLancamento;
	}

	public Date getYdthrFinal() {
		return ydthrFinal;
	}

	public void setYdthrFinal(Date ydthrFinal) {
		this.ydthrFinal = ydthrFinal;
	}

	public Integer getYtmiSeqProx() {
		return ytmiSeqProx;
	}

	public void setYtmiSeqProx(Integer ytmiSeqProx) {
		this.ytmiSeqProx = ytmiSeqProx;
	}

	public Integer getYcctCodigo() {
		return ycctCodigo;
	}

	public void setYcctCodigo(Integer ycctCodigo) {
		this.ycctCodigo = ycctCodigo;
	}

	public Integer getYclcCodigo() {
		return yclcCodigo;
	}

	public void setYclcCodigo(Integer yclcCodigo) {
		this.yclcCodigo = yclcCodigo;
	}

	public Integer getYclcCodigoLto() {
		return yclcCodigoLto;
	}

	public void setYclcCodigoLto(Integer yclcCodigoLto) {
		this.yclcCodigoLto = yclcCodigoLto;
	}

	public Integer getYclcCodigoPac() {
		return yclcCodigoPac;
	}

	public void setYclcCodigoPac(Integer yclcCodigoPac) {
		this.yclcCodigoPac = yclcCodigoPac;
	}

	public Integer getYphiSeq() {
		return yphiSeq;
	}

	public void setYphiSeq(Integer yphiSeq) {
		this.yphiSeq = yphiSeq;
	}

	public Integer getYgphSeq() {
		return ygphSeq;
	}

	public void setYgphSeq(Integer ygphSeq) {
		this.ygphSeq = ygphSeq;
	}

	public Integer getYcddCodigo() {
		return ycddCodigo;
	}

	public void setYcddCodigo(Integer ycddCodigo) {
		this.ycddCodigo = ycddCodigo;
	}

	public Integer getYbclBaiCodigo() {
		return ybclBaiCodigo;
	}

	public void setYbclBaiCodigo(Integer ybclBaiCodigo) {
		this.ybclBaiCodigo = ybclBaiCodigo;
	}

	public Integer getYdstCodigo() {
		return ydstCodigo;
	}

	public void setYdstCodigo(Integer ydstCodigo) {
		this.ydstCodigo = ydstCodigo;
	}

	public Boolean getYindConsClinLto() {
		return yindConsClinLto;
	}

	public void setYindConsClinLto(Boolean yindConsClinLto) {
		this.yindConsClinLto = yindConsClinLto;
	}

	public DominioSimNao getYindConsClinQrt() {
		return yindConsClinQrt;
	}

	public void setYindConsClinQrt(DominioSimNao yindConsClinQrt) {
		this.yindConsClinQrt = yindConsClinQrt;
	}

	public DominioSimNao getYindConsClinUnf() {
		return yindConsClinUnf;
	}

	public void setYindConsClinUnf(DominioSimNao yindConsClinUnf) {
		this.yindConsClinUnf = yindConsClinUnf;
	}

	public DominioSimNao getYindExclusivInfeccao() {
		return yindExclusivInfeccao;
	}

	public void setYindExclusivInfeccao(DominioSimNao yindExclusivInfeccao) {
		this.yindExclusivInfeccao = yindExclusivInfeccao;
	}

	public Short getYcnvCodigo() {
		return ycnvCodigo;
	}

	public void setYcnvCodigo(Short ycnvCodigo) {
		this.ycnvCodigo = ycnvCodigo;
	}

	public Integer getXprontuario() {
		return xprontuario;
	}

	public void setXprontuario(Integer xprontuario) {
		this.xprontuario = xprontuario;
	}

	public Integer getYprontuario() {
		return yprontuario;
	}

	public void setYprontuario(Integer yprontuario) {
		this.yprontuario = yprontuario;
	}

	public String getXltoPrivativo() {
		return xltoPrivativo;
	}

	public void setXltoPrivativo(String xltoPrivativo) {
		this.xltoPrivativo = xltoPrivativo;
	}

	public String getYltoPrivativo() {
		return yltoPrivativo;
	}

	public void setYltoPrivativo(String yltoPrivativo) {
		this.yltoPrivativo = yltoPrivativo;
	}

	public Boolean getNaoFim() {
		return naoFim;
	}

	public void setNaoFim(Boolean naoFim) {
		this.naoFim = naoFim;
	}

	public Boolean getNaoAlta() {
		return naoAlta;
	}

	public void setNaoAlta(Boolean naoAlta) {
		this.naoAlta = naoAlta;
	}

	public Boolean getNaoQuebra() {
		return naoQuebra;
	}

	public void setNaoQuebra(Boolean naoQuebra) {
		this.naoQuebra = naoQuebra;
	}

	public String getXdata() {
		return xdata;
	}

	public void setXdata(String xdata) {
		this.xdata = xdata;
	}

	public String getXsigla() {
		return xsigla;
	}

	public void setXsigla(String xsigla) {
		this.xsigla = xsigla;
	}

	public String getYsigla() {
		return ysigla;
	}

	public void setYsigla(String ysigla) {
		this.ysigla = ysigla;
	}

	public String getYdata() {
		return ydata;
	}

	public void setYdata(String ydata) {
		this.ydata = ydata;
	}

	public Date getXdatai() {
		return xdatai;
	}

	public void setXdatai(Date xdatai) {
		this.xdatai = xdatai;
	}

	public Date getXdataf() {
		return xdataf;
	}

	public void setXdataf(Date xdataf) {
		this.xdataf = xdataf;
	}

	public BigDecimal getXpacDia() {
		return xpacDia;
	}

	public void setXpacDia(BigDecimal xpacDia) {
		this.xpacDia = xpacDia;
	}

	public BigDecimal getXpacDiaRefl() {
		return xpacDiaRefl;
	}

	public void setXpacDiaRefl(BigDecimal xpacDiaRefl) {
		this.xpacDiaRefl = xpacDiaRefl;
	}

	public Integer getXltoDia() {
		return xltoDia;
	}

	public void setXltoDia(Integer xltoDia) {
		this.xltoDia = xltoDia;
	}

	public Integer getXadm() {
		return xadm;
	}

	public void setXadm(Integer xadm) {
		this.xadm = xadm;
	}

	public Integer getXasat() {
		return xasat;
	}

	public void setXasat(Integer xasat) {
		this.xasat = xasat;
	}

	public Short getXoutClinE() {
		return xoutClinE;
	}

	public void setXoutClinE(Short xoutClinE) {
		this.xoutClinE = xoutClinE;
	}

	public Short getXoutClinS() {
		return xoutClinS;
	}

	public void setXoutClinS(Short xoutClinS) {
		this.xoutClinS = xoutClinS;
	}

	public Short getXoutUnidE() {
		return xoutUnidE;
	}

	public void setXoutUnidE(Short xoutUnidE) {
		this.xoutUnidE = xoutUnidE;
	}

	public Short getXoutUnidS() {
		return xoutUnidS;
	}

	public void setXoutUnidS(Short xoutUnidS) {
		this.xoutUnidS = xoutUnidS;
	}

	public Short getXoutEspE() {
		return xoutEspE;
	}

	public void setXoutEspE(Short xoutEspE) {
		this.xoutEspE = xoutEspE;
	}

	public Short getXoutEspS() {
		return xoutEspS;
	}

	public void setXoutEspS(Short xoutEspS) {
		this.xoutEspS = xoutEspS;
	}

	public Short getXoutProfE() {
		return xoutProfE;
	}

	public void setXoutProfE(Short xoutProfE) {
		this.xoutProfE = xoutProfE;
	}

	public Short getXoutProfS() {
		return xoutProfS;
	}

	public void setXoutProfS(Short xoutProfS) {
		this.xoutProfS = xoutProfS;
	}

	public Integer getXalt() {
		return xalt;
	}

	public void setXalt(Integer xalt) {
		this.xalt = xalt;
	}

	public Short getXobtC() {
		return xobtC;
	}

	public void setXobtC(Short xobtC) {
		this.xobtC = xobtC;
	}

	public Integer getXobtD() {
		return xobtD;
	}

	public void setXobtD(Integer xobtD) {
		this.xobtD = xobtD;
	}

	public Short getXsdo() {
		return xsdo;
	}

	public void setXsdo(Short xsdo) {
		this.xsdo = xsdo;
	}

	public Integer getXpacMesAnt() {
		return xpacMesAnt;
	}

	public void setXpacMesAnt(Integer xpacMesAnt) {
		this.xpacMesAnt = xpacMesAnt;
	}

	public Integer getXblq() {
		return xblq;
	}

	public void setXblq(Integer xblq) {
		this.xblq = xblq;
	}

	public Integer getXcapcReflClin() {
		return xcapcReflClin;
	}

	public void setXcapcReflClin(Integer xcapcReflClin) {
		this.xcapcReflClin = xcapcReflClin;
	}

	public Integer getXsaidas() {
		return xsaidas;
	}

	public void setXsaidas(Integer xsaidas) {
		this.xsaidas = xsaidas;
	}

	public Integer getXentradas() {
		return xentradas;
	}

	public void setXentradas(Integer xentradas) {
		this.xentradas = xentradas;
	}

	public String getvOutClin() {
		return vOutClin;
	}

	public void setvOutClin(String vOutClin) {
		this.vOutClin = vOutClin;
	}

	public String getvOrgmAsat() {
		return vOrgmAsat;
	}

	public void setvOrgmAsat(String vOrgmAsat) {
		this.vOrgmAsat = vOrgmAsat;
	}

	public String getvOrgmBlc() {
		return vOrgmBlc;
	}

	public void setvOrgmBlc(String vOrgmBlc) {
		this.vOrgmBlc = vOrgmBlc;
	}

	public String getxOutClin() {
		return xOutClin;
	}

	public void setxOutClin(String xOutClin) {
		this.xOutClin = xOutClin;
	}

	public String getXtipo() {
		return xtipo;
	}

	public void setXtipo(String xtipo) {
		this.xtipo = xtipo;
	}

	public String getYtipo() {
		return ytipo;
	}

	public void setYtipo(String ytipo) {
		this.ytipo = ytipo;
	}

	public BigDecimal getvCapacidade() {
		return vCapacidade;
	}

	public void setvCapacidade(BigDecimal vCapacidade) {
		this.vCapacidade = vCapacidade;
	}

	public Integer getvLtoDia() {
		return vLtoDia;
	}

	public void setvLtoDia(Integer vLtoDia) {
		this.vLtoDia = vLtoDia;
	}

	public Integer getvOcupacao() {
		return vOcupacao;
	}

	public void setvOcupacao(Integer vOcupacao) {
		this.vOcupacao = vOcupacao;
	}

	public Integer getvCommit() {
		return vCommit;
	}

	public void setvCommit(Integer vCommit) {
		this.vCommit = vCommit;
	}

	public void incrIndUnid() {
		if (this.indUnid == null) {
			this.indUnid = 0;
		}
		this.indUnid++;
	}

	public void incrIndUnidPriv() {
		if (indUnidPriv == null) {
			this.indUnidPriv = 0;
		}
		this.indUnidPriv++;
	}

	public Map<Short, UnidadeFuncionalIndicadoresVO> getTabUnid() {
		return tabUnid;
	}

	public void setTabUnid(Map<Short, UnidadeFuncionalIndicadoresVO> tabUnid) {
		this.tabUnid = tabUnid;
	}

	public Map<Short, LeitoIndicadoresVO> getTabUnidPriv() {
		return tabUnidPriv;
	}

	public void setTabUnidPriv(Map<Short, LeitoIndicadoresVO> tabUnidPriv) {
		this.tabUnidPriv = tabUnidPriv;
	}

	public Map<Integer, ClinicaIndicadoresVO> getTabClinicas() {
		return tabClinicas;
	}

	public void setTabClinicas(Map<Integer, ClinicaIndicadoresVO> tabClinicas) {
		this.tabClinicas = tabClinicas;
	}

	public Map<Integer, AinIndicadoresHospitalares> getTabIndicadores() {
		return tabIndicadores;
	}

	public void setTabIndicadores(
			Map<Integer, AinIndicadoresHospitalares> tabIndicadores) {
		this.tabIndicadores = tabIndicadores;
	}

	public Map<Short, CapacidadeEspecialidadeIndicadoresVO> getTabEsp() {
		return tabEsp;
	}

	public void setTabEsp(
			Map<Short, CapacidadeEspecialidadeIndicadoresVO> tabEsp) {
		this.tabEsp = tabEsp;
	}

	public Map<Short, LeitosDesativadosIndicadoresVO> getTabDesat() {
		return tabDesat;
	}

	public void setTabDesat(Map<Short, LeitosDesativadosIndicadoresVO> tabDesat) {
		this.tabDesat = tabDesat;
	}

	public Map<Integer, LeitosBloqueadosIndicadoresVO> getTabBlq() {
		return tabBlq;
	}

	public void setTabBlq(Map<Integer, LeitosBloqueadosIndicadoresVO> tabBlq) {
		this.tabBlq = tabBlq;
	}

	public List<String> getValoresLog() {
		return valoresLog;
	}

	public void setValoresLog(List<String> valoresLog) {
		this.valoresLog = valoresLog;
	}

	public Integer getCountLog() {
		return countLog;
	}

	public void setCountLog(Integer countLog) {
		this.countLog = countLog;
	}
	
	public void addElementoValoresLog(String s) {
		if (this.valoresLog == null) {
			this.valoresLog = new ArrayList<String>();
		}
		this.valoresLog.add(s);
	}
	
	public void incrementarCountLog() {
		if (this.countLog == null) {
			this.countLog = 0;
		}
		this.countLog++;
	}

}
