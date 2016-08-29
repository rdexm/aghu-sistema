package br.gov.mec.aghu.faturamento.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;


public class ConsultarContaHospitalarController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3193233867365911109L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private VFatContaHospitalarPac fatContaHospitalarPacView;

	private String ssmSolicitado;

	private String financiamentoSolicitado;

	private String competencia;

	protected Integer cthSeq;

	private String valorTotal;

	private String clinica;
	
	protected String origem;

	private final String PageConsultarContaHospitalarList = "faturamento-consultarContaHospitalarList";
	private final String PageConsultarContaHospitalar = "faturamento-consultarContaHospitalar";
	private final String PageConsultarEspelhoAihList = "faturamento-espelho";
	private final String PageConsultarFatLogErrorList = "faturamento-consultarFatLogError";
	private final String PageLancarItensContaHospitalarList = "faturamento-lancarItensContaHospitalarList";
	
	public void inicio() {
	 

		fatContaHospitalarPacView = faturamentoFacade.obterContaHospitalarPaciente(cthSeq);
		
		String mes = obterMes();
		obterCompetencia(mes);
			
		this.obterFatEspelhoAih();
		final FatContasHospitalares conta = obterContaHospitalar();

		final Double valor = conta.getValorSh().doubleValue() + conta.getValorUti().doubleValue() + conta.getValorUtie().doubleValue()
		+ conta.getValorSp().doubleValue() + conta.getValorAcomp().doubleValue() + conta.getValorRn().doubleValue()
		+ conta.getValorSadt().doubleValue() + conta.getValorHemat().doubleValue() + conta.getValorTransp().doubleValue()
		+ conta.getValorOpm().doubleValue() + conta.getValorAnestesista().doubleValue() + conta.getValorProcedimento().doubleValue();

		valorTotal = AghuNumberFormat.formatarNumeroMoeda(valor);

		if (fatContaHospitalarPacView.getConvenioSaudePlano() != null) {
			fatContaHospitalarPacView.setSsmRealizado(faturamentoFacade.buscaSSM(fatContaHospitalarPacView.getCthSeq(), fatContaHospitalarPacView
					.getConvenioSaudePlano().getId().getCnvCodigo(), fatContaHospitalarPacView.getConvenioSaudePlano().getId().getSeq(),
					DominioSituacaoSSM.R));

			fatContaHospitalarPacView.setSsmSolicitado(faturamentoFacade.buscaSSM(fatContaHospitalarPacView.getCthSeq(), fatContaHospitalarPacView
					.getConvenioSaudePlano().getId().getCnvCodigo(), fatContaHospitalarPacView.getConvenioSaudePlano().getId().getSeq(),
					DominioSituacaoSSM.S));
			
			fatContaHospitalarPacView.setSituacaoSms(faturamentoFacade.buscaSitSms(fatContaHospitalarPacView.getCthSeq()));

			if (fatContaHospitalarPacView.getProcedimentoHospitalarInterno() != null
					&& fatContaHospitalarPacView.getProcedimentoHospitalarInternoRealizado() != null) {
				fatContaHospitalarPacView.setFinanciamentoRealizado(faturamentoFacade.buscaSsmComplexidade(fatContaHospitalarPacView.getCthSeq(),
						fatContaHospitalarPacView.getConvenioSaudePlano().getId().getCnvCodigo(), fatContaHospitalarPacView.getConvenioSaudePlano()
						.getId().getSeq(), fatContaHospitalarPacView.getProcedimentoHospitalarInterno().getSeq(), fatContaHospitalarPacView
						.getProcedimentoHospitalarInternoRealizado().getSeq(), DominioSituacaoSSM.R));

				fatContaHospitalarPacView.setFinanciamentoSolicitado(faturamentoFacade.buscaSsmComplexidade(fatContaHospitalarPacView.getCthSeq(),
						fatContaHospitalarPacView.getConvenioSaudePlano().getId().getCnvCodigo(), fatContaHospitalarPacView.getConvenioSaudePlano()
						.getId().getSeq(), fatContaHospitalarPacView.getProcedimentoHospitalarInterno().getSeq(), fatContaHospitalarPacView
						.getProcedimentoHospitalarInternoRealizado().getSeq(), DominioSituacaoSSM.S));
			}
		}
	
	}

	private String obterMes() {
		String mes = "";
		if (fatContaHospitalarPacView.getDciCpeMes() != null && fatContaHospitalarPacView.getDciCpeMes() < 10) {
			mes = "0" + fatContaHospitalarPacView.getDciCpeMes().toString();
		} else if (fatContaHospitalarPacView.getDciCpeMes() != null) {
			mes = fatContaHospitalarPacView.getDciCpeMes().toString();
		}
		
		return mes;
	}
	
	private void obterCompetencia(String mes) {
		competencia = "";
		if (fatContaHospitalarPacView.getDciCpeMes() != null && fatContaHospitalarPacView.getDciCpeAno() != null) {
			competencia = mes + "/" + fatContaHospitalarPacView.getDciCpeAno();
		}
	}
	
	private FatContasHospitalares obterContaHospitalar() {
		final FatContasHospitalares conta = fatContaHospitalarPacView.getContaHospitalar();

		conta.setValorSh((BigDecimal)CoreUtil.nvl(conta.getValorSh(), BigDecimal.ZERO));
		conta.setValorUti((BigDecimal)CoreUtil.nvl(conta.getValorUti(), BigDecimal.ZERO));
		conta.setValorUtie((BigDecimal)CoreUtil.nvl(conta.getValorUtie(), BigDecimal.ZERO));
		conta.setValorSp((BigDecimal)CoreUtil.nvl(conta.getValorSp(), BigDecimal.ZERO));
		conta.setValorAcomp((BigDecimal)CoreUtil.nvl(conta.getValorAcomp(), BigDecimal.ZERO));
		conta.setValorRn((BigDecimal)CoreUtil.nvl(conta.getValorRn(), BigDecimal.ZERO));
		conta.setValorSadt((BigDecimal)CoreUtil.nvl(conta.getValorSadt(), BigDecimal.ZERO));
		conta.setValorHemat((BigDecimal)CoreUtil.nvl(conta.getValorHemat(), BigDecimal.ZERO));
		conta.setValorTransp((BigDecimal)CoreUtil.nvl(conta.getValorTransp(), BigDecimal.ZERO));
		conta.setValorOpm((BigDecimal)CoreUtil.nvl(conta.getValorOpm(), BigDecimal.ZERO));
		conta.setValorAnestesista((BigDecimal)CoreUtil.nvl(conta.getValorAnestesista(), BigDecimal.ZERO));
		conta.setValorProcedimento((BigDecimal)CoreUtil.nvl(conta.getValorProcedimento(), BigDecimal.ZERO));

		return conta;
	}
	
	private void obterFatEspelhoAih() {
		List<FatEspelhoAih> fatEspelhoAih = new ArrayList<FatEspelhoAih>(fatContaHospitalarPacView.getFatEspelhoAih());
		if (!fatEspelhoAih.isEmpty() ) {
			FatDocumentoCobrancaAihs docCobAih = null;
			if (fatEspelhoAih.get(0).getDciCodigoDcih() != null) {
				docCobAih = faturamentoFacade.pesquisarFatDocumentoCobrancaAihsPorCodigoDcih(fatEspelhoAih.get(0).getDciCodigoDcih());
			}
			
			if (docCobAih != null) {
				final AghClinicas clinicas = aghuFacade.pesquisarAghClinicasPorCodigo((int) docCobAih.getClcCodigo());
				clinica = clinicas.getDescricao();
			}
		}
	}
	
	public String espelho() {
		return PageConsultarEspelhoAihList;
	}

	public String inconsistencia() {
		return PageConsultarFatLogErrorList;
	}

	public String voltar() {
		if (!origem.equals("")) {
			return origem;
		}
		return PageConsultarContaHospitalarList;
	}

	public String cid() {
		return PageLancarItensContaHospitalarList;
	}

	public VFatContaHospitalarPac getFatContaHospitalarPacView() {
		return fatContaHospitalarPacView;
	}

	public void setFatContaHospitalarPacView(VFatContaHospitalarPac fatContaHospitalarPacView) {
		this.fatContaHospitalarPacView = fatContaHospitalarPacView;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public String getSsmSolicitado() {
		return ssmSolicitado;
	}

	public void setSsmSolicitado(String ssmSolicitado) {
		this.ssmSolicitado = ssmSolicitado;
	}

	public String getFinanciamentoSolicitado() {
		return financiamentoSolicitado;
	}

	public void setFinanciamentoSolicitado(String financiamentoSolicitado) {
		this.financiamentoSolicitado = financiamentoSolicitado;
	}

	public String getCompetencia() {
		return competencia;
	}

	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}

	public String getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(String valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getClinica() {
		return clinica;
	}

	public void setClinica(String clinica) {
		this.clinica = clinica;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getPageConsultarContaHospitalarList() {
		return PageConsultarContaHospitalarList;
	}

	public String getPageConsultarContaHospitalar() {
		return PageConsultarContaHospitalar;
	}

	public String getPageConsultarEspelhoAihList() {
		return PageConsultarEspelhoAihList;
	}

	public String getPageConsultarFatLogErrorList() {
		return PageConsultarFatLogErrorList;
	}

	public String getPageLancarItensContaHospitalarList() {
		return PageLancarItensContaHospitalarList;
	}
}