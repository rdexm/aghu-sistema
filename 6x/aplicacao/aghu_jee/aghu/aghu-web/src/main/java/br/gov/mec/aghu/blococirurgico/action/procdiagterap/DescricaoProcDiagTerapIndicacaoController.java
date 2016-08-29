package br.gov.mec.aghu.blococirurgico.action.procdiagterap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.model.PdtSolicTemp;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;


public class DescricaoProcDiagTerapIndicacaoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapIndicacaoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 828736243110522092L;
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;

	
	private PdtSolicTemp solicitacao;
	
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;
	
	public void iniciar(DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		solicitacao = this.blocoCirurgicoProcDiagTerapFacade.obterSolicTempPorDdtSeq(descricaoProcDiagTerapVO.getDdtSeq());
		if(solicitacao == null) {
			solicitacao = new PdtSolicTemp();
			solicitacao.setDdtSeq(descricaoProcDiagTerapVO.getDdtSeq());
		}
	}
	
	public void gravar() {
		if(!StringUtils.isEmpty(solicitacao.getMotivo())) {
			solicitacao.setMotivo(solicitacao.getMotivo().replaceAll("\\r\\n", "\n"));
		}

		if(!StringUtils.isEmpty(solicitacao.getObservacoes())) {
			solicitacao.setObservacoes(solicitacao.getObservacoes().replaceAll("\\r\\n", "\n"));
		}
		
		this.blocoCirurgicoProcDiagTerapFacade.persistirIndicacao(solicitacao);
		//apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DESCRICAO_PROC_DIAG_TERAP_INDICACAO_ALTERADA_COM_SUCESSO");
		LOG.info("MENSAGEM_DESCRICAO_PROC_DIAG_TERAP_INDICACAO_ALTERADA_COM_SUCESSO");
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}

	public PdtSolicTemp getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(PdtSolicTemp solicitacao) {
		this.solicitacao = solicitacao;
	}
}
