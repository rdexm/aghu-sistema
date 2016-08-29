package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.model.FatEspelhoAih;

/**
 * Representa as variáveis <b>"globais da package <code>FATK_CTH3_RN_UN</code>"</b>. As mesmas são
 * inicializadas na <code>RN_CTHC_ATU_GERA_AIH</code> e lidas na <code>RN_CTHC_ATU_INS_AAM</code>, por
 * isso foi criado este VO que será inicializado na <code>RN_CTHC_ATU_GERA_AIH</code> e
 * passado como parâmetro para a <code>RN_CTHC_ATU_INS_AAM</code>.
 * 
 * @author lcmoura
 * 
 */

public class RnCthcAtuGeraAihVO {
	
	private Integer eaiSeqp;
	private Integer maxEai;
	private Integer maxAam;
	private FatEspelhoAih regEspelho; 
	
	private BigDecimal percFideps = BigDecimal.ONE;
	
	private FatEspelhoItemContaHospVO regItem;
	
	private BigDecimal vlrSh = BigDecimal.ZERO;    
	private BigDecimal vlrSp = BigDecimal.ZERO;    
	private BigDecimal vlrSadt = BigDecimal.ZERO;    
	private BigDecimal vlrAnest = BigDecimal.ZERO;    
	private BigDecimal vlrProced = BigDecimal.ZERO;    
	private Integer ptosAnest = Integer.valueOf(0);    
	private Integer ptosCirur = Integer.valueOf(0);    
	private Integer ptosSadt = Integer.valueOf(0);    
	private BigDecimal vlrUtie = BigDecimal.ZERO;    
	private BigDecimal vlrRn = BigDecimal.ZERO;    
	private BigDecimal vlrHemat = BigDecimal.ZERO;    
	private BigDecimal vlrTransp = BigDecimal.ZERO;    
	private BigDecimal vlrOpm = BigDecimal.ZERO;    
	private BigDecimal vlrShUtie = BigDecimal.ZERO;    
	private BigDecimal vlrShRn = BigDecimal.ZERO;    
	private BigDecimal vlrShHemat = BigDecimal.ZERO;    
	private BigDecimal vlrShTransp = BigDecimal.ZERO;    
	private BigDecimal vlrShOpm = BigDecimal.ZERO;    
	private BigDecimal vlrSpUtie = BigDecimal.ZERO;    
	private BigDecimal vlrSpRn = BigDecimal.ZERO;    
	private BigDecimal vlrSpHemat = BigDecimal.ZERO;    
	private BigDecimal vlrSpTransp = BigDecimal.ZERO;    
	private BigDecimal vlrSpOpm = BigDecimal.ZERO;    
	private BigDecimal vlrSadtUtie = BigDecimal.ZERO;    
	private BigDecimal vlrSadtRn = BigDecimal.ZERO;    
	private BigDecimal vlrSadtHemat = BigDecimal.ZERO;    
	private BigDecimal vlrSadtTransp = BigDecimal.ZERO;    
	private BigDecimal vlrSadtOpm = BigDecimal.ZERO;    
	private BigDecimal vlrProcUtie = BigDecimal.ZERO;    
	private BigDecimal vlrProcRn = BigDecimal.ZERO;    
	private BigDecimal vlrProcHemat = BigDecimal.ZERO;    
	private BigDecimal vlrProcTransp = BigDecimal.ZERO;    
	private BigDecimal vlrProcOpm = BigDecimal.ZERO;    
	private BigDecimal vlrAnestUtie = BigDecimal.ZERO;    
	private BigDecimal vlrAnestRn = BigDecimal.ZERO;    
	private BigDecimal vlrAnestHemat = BigDecimal.ZERO;    
	private BigDecimal vlrAnestTransp = BigDecimal.ZERO;    
	private BigDecimal vlrAnestOpm = BigDecimal.ZERO;
	
	private Byte codAtoUtie;    
	private Byte codAtoRn; 
	private Byte codAtoConsPed; 
	private Byte codAtoHemat; 
	private Byte codAtoOpm; 
	private Byte codAtoSalaTransp; 
	private Byte codAtoSadtTransp; 
	private Byte codAtoCirurTransp;
	
	public Integer getEaiSeqp() {
		return eaiSeqp;
	}
	public Integer getMaxEai() {
		return maxEai;
	}
	public Integer getMaxAam() {
		return maxAam;
	}
	public FatEspelhoAih getRegEspelho() {
		return regEspelho;
	}
	public BigDecimal getPercFideps() {
		return percFideps;
	}
	public FatEspelhoItemContaHospVO getRegItem() {
		return regItem;
	}
	public BigDecimal getVlrSh() {
		return vlrSh;
	}
	public BigDecimal getVlrSp() {
		return vlrSp;
	}
	public BigDecimal getVlrSadt() {
		return vlrSadt;
	}
	public BigDecimal getVlrAnest() {
		return vlrAnest;
	}
	public BigDecimal getVlrProced() {
		return vlrProced;
	}
	public Integer getPtosAnest() {
		return ptosAnest;
	}
	public Integer getPtosCirur() {
		return ptosCirur;
	}
	public Integer getPtosSadt() {
		return ptosSadt;
	}
	public BigDecimal getVlrUtie() {
		return vlrUtie;
	}
	public BigDecimal getVlrRn() {
		return vlrRn;
	}
	public BigDecimal getVlrHemat() {
		return vlrHemat;
	}
	public BigDecimal getVlrTransp() {
		return vlrTransp;
	}
	public BigDecimal getVlrOpm() {
		return vlrOpm;
	}
	public BigDecimal getVlrShUtie() {
		return vlrShUtie;
	}
	public BigDecimal getVlrShRn() {
		return vlrShRn;
	}
	public BigDecimal getVlrShHemat() {
		return vlrShHemat;
	}
	public BigDecimal getVlrShTransp() {
		return vlrShTransp;
	}
	public BigDecimal getVlrShOpm() {
		return vlrShOpm;
	}
	public BigDecimal getVlrSpUtie() {
		return vlrSpUtie;
	}
	public BigDecimal getVlrSpRn() {
		return vlrSpRn;
	}
	public BigDecimal getVlrSpHemat() {
		return vlrSpHemat;
	}
	public BigDecimal getVlrSpTransp() {
		return vlrSpTransp;
	}
	public BigDecimal getVlrSpOpm() {
		return vlrSpOpm;
	}
	public BigDecimal getVlrSadtUtie() {
		return vlrSadtUtie;
	}
	public BigDecimal getVlrSadtRn() {
		return vlrSadtRn;
	}
	public BigDecimal getVlrSadtHemat() {
		return vlrSadtHemat;
	}
	public BigDecimal getVlrSadtTransp() {
		return vlrSadtTransp;
	}
	public BigDecimal getVlrSadtOpm() {
		return vlrSadtOpm;
	}
	public BigDecimal getVlrProcUtie() {
		return vlrProcUtie;
	}
	public BigDecimal getVlrProcRn() {
		return vlrProcRn;
	}
	public BigDecimal getVlrProcHemat() {
		return vlrProcHemat;
	}
	public BigDecimal getVlrProcTransp() {
		return vlrProcTransp;
	}
	public BigDecimal getVlrProcOpm() {
		return vlrProcOpm;
	}
	public BigDecimal getVlrAnestUtie() {
		return vlrAnestUtie;
	}
	public BigDecimal getVlrAnestRn() {
		return vlrAnestRn;
	}
	public BigDecimal getVlrAnestHemat() {
		return vlrAnestHemat;
	}
	public BigDecimal getVlrAnestTransp() {
		return vlrAnestTransp;
	}
	public BigDecimal getVlrAnestOpm() {
		return vlrAnestOpm;
	}
	public Byte getCodAtoUtie() {
		return codAtoUtie;
	}
	public Byte getCodAtoRn() {
		return codAtoRn;
	}
	public Byte getCodAtoConsPed() {
		return codAtoConsPed;
	}
	public Byte getCodAtoHemat() {
		return codAtoHemat;
	}
	public Byte getCodAtoOpm() {
		return codAtoOpm;
	}
	public Byte getCodAtoSalaTransp() {
		return codAtoSalaTransp;
	}
	public Byte getCodAtoSadtTransp() {
		return codAtoSadtTransp;
	}
	public Byte getCodAtoCirurTransp() {
		return codAtoCirurTransp;
	}
	public void setEaiSeqp(Integer eaiSeqp) {
		this.eaiSeqp = eaiSeqp;
	}
	public void setMaxEai(Integer maxEai) {
		this.maxEai = maxEai;
	}
	public void setMaxAam(Integer maxAam) {
		this.maxAam = maxAam;
	}
	public void setRegEspelho(FatEspelhoAih regEspelho) {
		this.regEspelho = regEspelho;
	}
	public void setPercFideps(BigDecimal percFideps) {
		this.percFideps = percFideps;
	}
	public void setRegItem(FatEspelhoItemContaHospVO regItem) {
		this.regItem = regItem;
	}
	public void setVlrSh(BigDecimal vlrSh) {
		this.vlrSh = vlrSh;
	}
	public void setVlrSp(BigDecimal vlrSp) {
		this.vlrSp = vlrSp;
	}
	public void setVlrSadt(BigDecimal vlrSadt) {
		this.vlrSadt = vlrSadt;
	}
	public void setVlrAnest(BigDecimal vlrAnest) {
		this.vlrAnest = vlrAnest;
	}
	public void setVlrProced(BigDecimal vlrProced) {
		this.vlrProced = vlrProced;
	}
	public void setPtosAnest(Integer ptosAnest) {
		this.ptosAnest = ptosAnest;
	}
	public void setPtosCirur(Integer ptosCirur) {
		this.ptosCirur = ptosCirur;
	}
	public void setPtosSadt(Integer ptosSadt) {
		this.ptosSadt = ptosSadt;
	}
	public void setVlrUtie(BigDecimal vlrUtie) {
		this.vlrUtie = vlrUtie;
	}
	public void setVlrRn(BigDecimal vlrRn) {
		this.vlrRn = vlrRn;
	}
	public void setVlrHemat(BigDecimal vlrHemat) {
		this.vlrHemat = vlrHemat;
	}
	public void setVlrTransp(BigDecimal vlrTransp) {
		this.vlrTransp = vlrTransp;
	}
	public void setVlrOpm(BigDecimal vlrOpm) {
		this.vlrOpm = vlrOpm;
	}
	public void setVlrShUtie(BigDecimal vlrShUtie) {
		this.vlrShUtie = vlrShUtie;
	}
	public void setVlrShRn(BigDecimal vlrShRn) {
		this.vlrShRn = vlrShRn;
	}
	public void setVlrShHemat(BigDecimal vlrShHemat) {
		this.vlrShHemat = vlrShHemat;
	}
	public void setVlrShTransp(BigDecimal vlrShTransp) {
		this.vlrShTransp = vlrShTransp;
	}
	public void setVlrShOpm(BigDecimal vlrShOpm) {
		this.vlrShOpm = vlrShOpm;
	}
	public void setVlrSpUtie(BigDecimal vlrSpUtie) {
		this.vlrSpUtie = vlrSpUtie;
	}
	public void setVlrSpRn(BigDecimal vlrSpRn) {
		this.vlrSpRn = vlrSpRn;
	}
	public void setVlrSpHemat(BigDecimal vlrSpHemat) {
		this.vlrSpHemat = vlrSpHemat;
	}
	public void setVlrSpTransp(BigDecimal vlrSpTransp) {
		this.vlrSpTransp = vlrSpTransp;
	}
	public void setVlrSpOpm(BigDecimal vlrSpOpm) {
		this.vlrSpOpm = vlrSpOpm;
	}
	public void setVlrSadtUtie(BigDecimal vlrSadtUtie) {
		this.vlrSadtUtie = vlrSadtUtie;
	}
	public void setVlrSadtRn(BigDecimal vlrSadtRn) {
		this.vlrSadtRn = vlrSadtRn;
	}
	public void setVlrSadtHemat(BigDecimal vlrSadtHemat) {
		this.vlrSadtHemat = vlrSadtHemat;
	}
	public void setVlrSadtTransp(BigDecimal vlrSadtTransp) {
		this.vlrSadtTransp = vlrSadtTransp;
	}
	public void setVlrSadtOpm(BigDecimal vlrSadtOpm) {
		this.vlrSadtOpm = vlrSadtOpm;
	}
	public void setVlrProcUtie(BigDecimal vlrProcUtie) {
		this.vlrProcUtie = vlrProcUtie;
	}
	public void setVlrProcRn(BigDecimal vlrProcRn) {
		this.vlrProcRn = vlrProcRn;
	}
	public void setVlrProcHemat(BigDecimal vlrProcHemat) {
		this.vlrProcHemat = vlrProcHemat;
	}
	public void setVlrProcTransp(BigDecimal vlrProcTransp) {
		this.vlrProcTransp = vlrProcTransp;
	}
	public void setVlrProcOpm(BigDecimal vlrProcOpm) {
		this.vlrProcOpm = vlrProcOpm;
	}
	public void setVlrAnestUtie(BigDecimal vlrAnestUtie) {
		this.vlrAnestUtie = vlrAnestUtie;
	}
	public void setVlrAnestRn(BigDecimal vlrAnestRn) {
		this.vlrAnestRn = vlrAnestRn;
	}
	public void setVlrAnestHemat(BigDecimal vlrAnestHemat) {
		this.vlrAnestHemat = vlrAnestHemat;
	}
	public void setVlrAnestTransp(BigDecimal vlrAnestTransp) {
		this.vlrAnestTransp = vlrAnestTransp;
	}
	public void setVlrAnestOpm(BigDecimal vlrAnestOpm) {
		this.vlrAnestOpm = vlrAnestOpm;
	}
	public void setCodAtoUtie(Byte codAtoUtie) {
		this.codAtoUtie = codAtoUtie;
	}
	public void setCodAtoRn(Byte codAtoRn) {
		this.codAtoRn = codAtoRn;
	}
	public void setCodAtoConsPed(Byte codAtoConsPed) {
		this.codAtoConsPed = codAtoConsPed;
	}
	public void setCodAtoHemat(Byte codAtoHemat) {
		this.codAtoHemat = codAtoHemat;
	}
	public void setCodAtoOpm(Byte codAtoOpm) {
		this.codAtoOpm = codAtoOpm;
	}
	public void setCodAtoSalaTransp(Byte codAtoSalaTransp) {
		this.codAtoSalaTransp = codAtoSalaTransp;
	}
	public void setCodAtoSadtTransp(Byte codAtoSadtTransp) {
		this.codAtoSadtTransp = codAtoSadtTransp;
	}
	public void setCodAtoCirurTransp(Byte codAtoCirurTransp) {
		this.codAtoCirurTransp = codAtoCirurTransp;
	}	
}
