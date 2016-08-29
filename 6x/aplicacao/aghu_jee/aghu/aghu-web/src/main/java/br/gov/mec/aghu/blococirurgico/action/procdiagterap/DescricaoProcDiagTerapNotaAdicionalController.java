package br.gov.mec.aghu.blococirurgico.action.procdiagterap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.model.PdtNotaAdicional;
import br.gov.mec.aghu.model.PdtNotaAdicionalId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 *
 * Controller para a aba Nota Adicional.
 * 
 * @author eschweigert
 *
 */

public class DescricaoProcDiagTerapNotaAdicionalController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapNotaAdicionalController.class);

	private static final long serialVersionUID = 928717147110522092L;
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private DescricaoProcDiagTerapVO descricaoProcDiagTerapVO;
	private PdtNotaAdicional notaAdicional;

	private RapServidores servidorLogado;
	

	private final String PAGE_BLOCO_LISTA_CIRURGIAS = "blococirurgico-listaCirurgias";
	
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;

	public void iniciar(DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
		
		
		try {
			servidorLogado = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		
		notaAdicional = blocoCirurgicoProcDiagTerapFacade.obterPdtNotaAdicionalPorServidorEPdtDescricao(servidorLogado, descricaoProcDiagTerapVO.getDdtSeq());
		notaAdicional = notaAdicional == null ? new PdtNotaAdicional() : notaAdicional;
	}

	public void processarNotaAdicional() {
		if(notaAdicional.getNotaAdicional() != null)	{				
			notaAdicional.setNotaAdicional(notaAdicional.getNotaAdicional().replaceAll("\\r\\n", "\n"));
		}		
	}

	public String salvarNotaAdicional(){
		try {
			if(notaAdicional.getNotaAdicional() != null)	{				
	
			//	boolean isInsert = false;
				
				processarNotaAdicional();
				
				if(notaAdicional.getId() == null){
				//	isInsert = true;
					notaAdicional.setId(new PdtNotaAdicionalId(descricaoProcDiagTerapVO.getDdtSeq(), null));
				}
				
				blocoCirurgicoProcDiagTerapFacade.persistirPdtNotaAdicional(notaAdicional);
				
				/*if(isInsert){
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NOTA_ADICIONAL_INSERT_SUCESSO");
					
				} else {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NOTA_ADICIONAL_SALVA_SUCESSO");
				}*/
				
				return PAGE_BLOCO_LISTA_CIRURGIAS;
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NOTA_ADICIONAL_SEM_DESCRICAO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	public String excluir(){
		try {
			
			if(notaAdicional != null && notaAdicional.getId() != null && notaAdicional.getNotaAdicional() != null){
				blocoCirurgicoProcDiagTerapFacade.excluirPdtNotaAdicional(notaAdicional);
				//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NOTA_ADICIONAL_EXCLUIDA_SUCESSO");
			}
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
			return PAGE_BLOCO_LISTA_CIRURGIAS;

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		
		return null;
	}
	

	public DescricaoProcDiagTerapVO getDescricaoProcDiagTerapVO() {
		return descricaoProcDiagTerapVO;
	}


	public void setDescricaoProcDiagTerapVO(
			DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
	}


	public PdtNotaAdicional getNotaAdicional() {
		return notaAdicional;
	}


	public void setNotaAdicional(PdtNotaAdicional notaAdicional) {
		this.notaAdicional = notaAdicional;
	}


	public RapServidores getServidorLogado() {
		return servidorLogado;
	}


	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
	
}