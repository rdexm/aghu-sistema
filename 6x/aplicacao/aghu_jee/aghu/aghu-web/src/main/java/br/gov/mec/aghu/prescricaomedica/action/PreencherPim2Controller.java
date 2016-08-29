package br.gov.mec.aghu.prescricaomedica.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoPim2;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ResultadoExamePim2VO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PreencherPim2Controller extends ActionController {

	private static final long serialVersionUID = -2712707147111910465L;

	private static final Log LOG = LogFactory.getLog(PreencherPim2Controller.class);

	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB 
	private IPacienteFacade pacienteFacade;

	@EJB 
	private IExamesFacade examesFacade;

	@EJB 
	private IAghuFacade aghuFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@Inject
	private VerificarPrescricaoMedicaController verificarPrescricaoMedicaController;
	
	private static final String PAGE_VERIFICAR_PRESCRICAO_MEDICA = "prescricaomedica-verificaPrescricaoMedica";
	
	private Integer atdSeq;
	private MpmPim2 pim2;
	private AipPacientes paciente;
	
	private String examePaO2;
	private String exameBase;
	
	private String idade;

	ResultadoExamePim2VO excessoBaseVO;
	ResultadoExamePim2VO paO2VO;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}		

	public void iniciar() {
		if(atdSeq != null) {
			AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
			paciente = pacienteFacade.obterPaciente(atendimento.getPaciente().getCodigo());
			idade = prontuarioOnlineFacade.buscarIdade(paciente.getDtNascimento(), new Date());
			List<MpmPim2> listaPim2 =  prescricaoMedicaFacade.pesquisarPim2PorAtendimento(atdSeq, DominioSituacaoPim2.P);
			if(listaPim2 != null && !listaPim2.isEmpty()) {
				pim2 = (MpmPim2)CollectionUtils.get(listaPim2, 0);
				if(DominioSituacaoPim2.P.equals(pim2.getSituacao())) {
					excessoBaseVO = examesFacade.obterExamePim2(atendimento.getSeq(), new String[] {"EB", "EB GASO 100%", "EB GASO PEDIÁTRICA", "EB GASO VENOSA", "EB GASO2"});
					if(excessoBaseVO != null) {
						exameBase = BigDecimal.valueOf(excessoBaseVO.getValor()).divide(BigDecimal.valueOf(10))  + " nmol/L – resultado em " + DateUtil.obterDataFormatada(excessoBaseVO.getDthrLiberada(), "dd/MM/yyyy") + " às " + DateUtil.obterDataFormatada(excessoBaseVO.getDthrLiberada(), "HH:mm");
					}
					paO2VO = examesFacade.obterExamePim2(atendimento.getSeq(), new String[] {"PaO2", "PO2", "PO2 GASO 100%", "PO2 GASO PEDIÁTRICA", "PO2 GASO VENOSA", "PO2 GASO2", "PO2A", "PO2E", "PO2O2"});
					if(paO2VO != null) {
						examePaO2 = BigDecimal.valueOf(paO2VO.getValor()).divide(BigDecimal.valueOf(10)) + " nmol/L – resultado em " + DateUtil.obterDataFormatada(paO2VO.getDthrLiberada(), "dd/MM/yyyy") + " às " + DateUtil.obterDataFormatada(paO2VO.getDthrLiberada(), "HH:mm");
					}
				}
			}
		}
	}

	public void copiarExcessoBase() {
		pim2.setExcessoBase(BigDecimal.valueOf(excessoBaseVO.getValor()).divide(BigDecimal.valueOf(10)));
	}

	public void copiarPaO2() {
		pim2.setPao2(BigDecimal.valueOf(paO2VO.getValor()).divide(BigDecimal.valueOf(10)));
	}

	protected boolean isCamposCalculoPreenchidos() {
		if(pim2.getPressaoSistolica() == null || pim2.getFaltaRespostaPupilar() == null || pim2.getFio2() == null || pim2.getPao2() == null
				|| pim2.getExcessoBase() == null || pim2.getVentilacaoMecanica() == null || pim2.getAdmissaoEletiva() == null 
				|| pim2.getAdmissaoPosBypass() == null || pim2.getAdmissaoRecuperaCirProc() == null || pim2.getDiagAltoRisco() == null
				|| pim2.getDiagBaixoRisco() == null) {
			return false;
		}
		
		return true;
	}
	
	public void realizarCalculos() {
		if(!this.isCamposCalculoPreenchidos()) {
			return;
		}

		this.calcularPim2();
		this.calcularProbabilidadeMortePim2();
	}
	
	protected void calcularPim2() {
		pim2.setEscorePim2(BigDecimal.valueOf((0.01395*Math.abs(pim2.getPressaoSistolica() - 120)) + (3.0791 * obterValorDominioSimNao(pim2.getFaltaRespostaPupilar())) 
				+ (0.2888 * (100 * (pim2.getFio2().doubleValue() / pim2.getPao2().doubleValue()))) 
				+ (0.104 * (Math.abs(pim2.getExcessoBase().doubleValue()))) + (1.3352 * obterValorDominioSimNao(pim2.getVentilacaoMecanica())) 
				- (0.9282 * obterValorDominioSimNao(pim2.getAdmissaoEletiva())) - (1.0244 * obterValorDominioSimNao(pim2.getAdmissaoRecuperaCirProc()))
				+ (0.7507 * obterValorDominioSimNao(pim2.getAdmissaoPosBypass())) + (1.6829 * pim2.getDiagAltoRisco().getCodigo()) - (1.5770 * pim2.getDiagBaixoRisco().getCodigo()) - 4.8841));
	}
	
	protected void calcularProbabilidadeMortePim2() 
	{
		pim2.setProbabilidadeMorte(BigDecimal.valueOf(Math.exp(pim2.getEscorePim2().doubleValue()) / (1 + Math.exp(pim2.getEscorePim2().doubleValue()))).multiply(BigDecimal.valueOf(100)));
	}

	protected void ajustarValores() {
		DecimalFormat df = new DecimalFormat("##0.00");
		try {
			pim2.setProbabilidadeMorte(BigDecimal.valueOf(df.parse(df.format(pim2.getProbabilidadeMorte())).doubleValue()));
			df = new DecimalFormat("#0.00000");
			pim2.setEscorePim2(BigDecimal.valueOf(df.parse(df.format(pim2.getEscorePim2())).doubleValue()));			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public String gravar() {
		try {
			pim2.setSituacao(DominioSituacaoPim2.E);
			pim2.setDthrRealizacao(new Date());
			this.ajustarValores();
			prescricaoMedicaFacade.atualizarPim2(pim2, null);
			
			verificarPrescricaoMedicaController.setAtendimentoSeq(atdSeq);
			
			this.apresentarMsgNegocio(Severity.INFO, "MPM_PIM_2_SALVO_SUCESSO");
			return verificarPrescricaoMedicaController.criarPrescricao();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	public String voltar(){
//		modalCentralMensagensController.iniciarModal();
		idade = null;
		return PAGE_VERIFICAR_PRESCRICAO_MEDICA;
	}
	
	protected Integer obterValorDominioSimNao(DominioSimNao dominio) {
		return DominioSimNao.S.equals(dominio) ? 1 : 0;
	}
	
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public MpmPim2 getPim2() {
		return pim2;
	}

	public void setPim2(MpmPim2 pim2) {
		this.pim2 = pim2;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getExamePaO2() {
		return examePaO2;
	}

	public void setExamePaO2(String examePaO2) {
		this.examePaO2 = examePaO2;
	}

	public String getExameBase() {
		return exameBase;
	}

	public void setExameBase(String exameBase) {
		this.exameBase = exameBase;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}
}
