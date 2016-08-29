package br.gov.mec.aghu.blococirurgico.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaVO;
import br.gov.mec.aghu.model.MbcNotaAdicionais;
import br.gov.mec.aghu.model.MbcNotaAdicionaisId;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoCirurgicaNotaAdicionalController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	private static final long serialVersionUID = 1495464471221112335L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
		
	private DescricaoCirurgicaVO descricaoCirurgicaVO;
	
	private MbcNotaAdicionais notaAdicional;
	
	private final String PAGE_BLOCO_LISTA_CIRURGIAS = "listarCirurgias";
	

	@Inject
	private	RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	public void iniciar(DescricaoCirurgicaVO descricaoCirurgicaVO){
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
		
		notaAdicional = blocoCirurgicoFacade.buscarNotaAdicionais(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp());
		notaAdicional = notaAdicional == null ? new MbcNotaAdicionais() : notaAdicional;
	}
	
	public void processarNotaAdicional() {
		if(notaAdicional.getNotasAdicionais() != null)	{				
			notaAdicional.setNotasAdicionais(notaAdicional.getNotasAdicionais().replaceAll("\\r\\n", "\n"));
			relatorioDescricaoCirurgiaController.inicio();
		}		
	}
	
	public String salvarNotaAdicional(){
		try {
			if(notaAdicional.getNotasAdicionais() != null)	{				
				
				//boolean isInsert = false;
				if(notaAdicional.getId() == null){
					//isInsert = true;
					notaAdicional.setId(new MbcNotaAdicionaisId(descricaoCirurgicaVO.getDcgCrgSeq(), descricaoCirurgicaVO.getDcgSeqp(), null));
				}
				
				blocoCirurgicoFacade.persistirMbcNotaAdicionais(notaAdicional);
				blocoCirurgicoFacade.mbcpImprimeDescrCirurgica(Boolean.FALSE);				
				
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
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
		
		return null;
	}
	
	public String excluir(){
		try {
			
			if(notaAdicional != null && notaAdicional.getId() != null && notaAdicional.getNotasAdicionais() != null){				
				blocoCirurgicoFacade.excluirMbcNotaAdicionais(notaAdicional);				
				//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NOTA_ADICIONAL_EXCLUIDA_SUCESSO");
				relatorioDescricaoCirurgiaController.inicio();
			}
			
			return PAGE_BLOCO_LISTA_CIRURGIAS;
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		
		return null;
	}

	public DescricaoCirurgicaVO getDescricaoCirurgicaVO() {
		return descricaoCirurgicaVO;
	}

	public void setDescricaoCirurgicaVO(DescricaoCirurgicaVO descricaoCirurgicaVO) {
		this.descricaoCirurgicaVO = descricaoCirurgicaVO;
	}

	public MbcNotaAdicionais getNotaAdicional() {
		return notaAdicional;
	}

	public void setNotaAdicional(MbcNotaAdicionais notaAdicional) {
		this.notaAdicional = notaAdicional;
	}
}
