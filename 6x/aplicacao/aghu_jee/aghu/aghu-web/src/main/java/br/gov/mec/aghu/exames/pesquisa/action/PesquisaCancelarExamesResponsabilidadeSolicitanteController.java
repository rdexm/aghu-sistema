package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.cadastrosapoio.action.CancelarExamesResponsabilidadeSolicitanteController;
import br.gov.mec.aghu.exames.pesquisa.business.ExameCancelarExceptionCode;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameResultadoVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class PesquisaCancelarExamesResponsabilidadeSolicitanteController extends ActionController {

	private static final Log LOG = LogFactory.getLog(PesquisaCancelarExamesResponsabilidadeSolicitanteController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6346870592944812296L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
	private CancelarExamesResponsabilidadeSolicitanteController cancelarExamesResponsabilidadeSolicitanteController;
	
	/*	fitro da tela de pesquisa	*/
	private PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();
	private List<VAelSolicAtendsVO> listaExamesCancelar = new ArrayList<VAelSolicAtendsVO>();
	private SolicitacaoExameResultadoVO exameResultadoVO;
//	private Integer totalRegistros = 0;
	private Integer soeSeq;

	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}	
	

	public void inicio() {
	 


		if(filtro.getNumeroSolicitacaoInfo()!=null){
			this.pesquisar();
		}

	
	}

	public String pesquisar() {
		
		try{
			
			if (filtro.isPreenchido()) {

				filtro.setServidorLogado(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				listaExamesCancelar = recuperarLista();
				
				if(listaExamesCancelar!= null && listaExamesCancelar.size()==1){
					this.soeSeq = listaExamesCancelar.get(0).getNumero();
					cancelarExamesResponsabilidadeSolicitanteController.setSoeSeq(soeSeq);
					return "cancelarExamesResponsabilidadeSolicitante";
					
				}else if(listaExamesCancelar == null || listaExamesCancelar.size()==0){
					throw new ApplicationBusinessException(ExameCancelarExceptionCode.AEL_01195);
				}

				
			} else {
				
				this.apresentarMsgNegocio(Severity.INFO, ExameCancelarExceptionCode.MENSAGEM_INFORMAR_CAMPO.toString());
				setListaExamesCancelar(null);
				
			}

		} catch (BaseException e) {
			//StatusMessages.instance().add(Severity.INFO, e.getMessage());
			LOG.info(e.getMessage(),e);
			super.apresentarExcecaoNegocio(e);

		}
		
		return null;
	}

	protected List<VAelSolicAtendsVO> recuperarLista() throws BaseException {
		
		exameResultadoVO = solicitacaoExameFacade.listarExamesCancelamentoSolicitante(this.filtro);

		if(exameResultadoVO != null && exameResultadoVO.getListaSolicitacaoExames().size()>0){

			listaExamesCancelar = exameResultadoVO.getListaSolicitacaoExames();
			//totalRegistros = exameResultadoVO.getTotalRegistros(); 

		}else{
			setListaExamesCancelar(null);
		}

		return listaExamesCancelar;
	}


	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}


	/**
	 * Metodo que limpa os campos de filtro<br>
	 * na tele de pesquisa de exames.
	 */
	public void limparPesquisa() {
		filtro = new PesquisaExamesFiltroVO();
		listaExamesCancelar = new ArrayList<VAelSolicAtendsVO>();
		//super.setAtivo(false);
	}

	public PesquisaExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaExamesFiltroVO filtro) {
		this.filtro = filtro;
	}

	public List<VAelSolicAtendsVO> getListaExamesCancelar() {
		return listaExamesCancelar;
	}

	public void setListaExamesCancelar(List<VAelSolicAtendsVO> listaExamesCancelar) {
		this.listaExamesCancelar = listaExamesCancelar;
	}


}