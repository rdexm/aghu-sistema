package br.gov.mec.aghu.sicon.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoConvItensContrato;
import br.gov.mec.aghu.model.ScoConvItensContratoId;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoItensContratoJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.dao.ScoAditContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoConvItensContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoItensContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoItensContratoJnDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * The Class ManterItensContratoON.
 */
@SuppressWarnings({"PMD.AtributoEmSeamContextManager"})
@Stateless
public class ManterItensContratoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterItensContratoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
		
	@Inject
	private ScoAditContratoDAO scoAditContratoDAO;
	
	@Inject
	private ScoConvItensContratoDAO scoConvItensContratoDAO;
	
	@Inject
	private ScoItensContratoJnDAO scoItensContratoJnDAO;
	
	@Inject
	private ScoItensContratoDAO scoItensContratoDAO;
	
	@Inject
	private ScoItensContratoDAO dao;
	
	@Inject
	private ScoConvItensContratoDAO convItensContratoDAO;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -5427749813718474996L;

	/**
	 * The Enum ManterItensContratoONExceptionCode.
	 */
	private enum ManterItensContratoONExceptionCode implements
	BusinessExceptionCode {
			CONTRATO_JA_ENVIADA_E_CONTRATO_TEM_ADITIVOS, EXCESSAO_POR_TAMANHO_UNIDADE;
	}
	
	
	/**
	 * Gravar atualizar.
	 *
	 * @param input the input
	 * @param fsoConveniosFinanceiro the fso convenios financeiro
	 * @return the sco itens contrato
	 *  the aGHU negocio exception
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ScoItensContrato gravarAtualizar(ScoItensContrato input, FsoConveniosFinanceiro fsoConveniosFinanceiro)throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoItensContratoDAO scoItensContratoDAO = this.getItensContratoDAO();
		ScoConvItensContratoDAO scoConvItensContratoDAO = this.getConvItensContratoDAO();
		
		propriedadesObrigatoriasPreenchidas(input);
		
		input.setVlTotal(input.getVlUnitario().multiply(new BigDecimal(input.getQuantidade())));
    	//RN01
    	if(!verificaRN01OK(input)) {
    		throw new ApplicationBusinessException(ManterItensContratoONExceptionCode.CONTRATO_JA_ENVIADA_E_CONTRATO_TEM_ADITIVOS);
    	}
    	
		if (excessaoPorTamanhoUnidade(input)){
			throw new ApplicationBusinessException(ManterItensContratoONExceptionCode.EXCESSAO_POR_TAMANHO_UNIDADE);
		}    	
    	
        if (isAtualizacao(input)){
        		//RN02
        		input.setAlteradoEm(new Date());
        		input.setServidor(servidorLogado);
        		ScoItensContrato res = scoItensContratoDAO.atualizar(input);
        		scoItensContratoDAO.flush();
        		
        		registrarItemContratoJn(res, DominioOperacoesJournal.UPD);
        		
        		if(res.getConvItensContrato()!=null && res.getConvItensContrato().size()==1){   		
	        		ScoConvItensContrato convcont = res.getConvItensContrato().get(0);
	        		input.setVlTotal(input.getVlUnitario().multiply(new BigDecimal(input.getQuantidade())));
	        		if(!convcont.getValorRateio().equals(input.getVlTotal())){
	        			convcont.setValorRateio(input.getVlTotal());
	        			scoConvItensContratoDAO.atualizar(convcont);
	        			scoConvItensContratoDAO.flush();
	        		}
        		}
        		return res;
        }
        else{
    		//RN02
            input.setCriadoEm(new Date());        	
            input.setServidor(servidorLogado);
            
            scoItensContratoDAO.persistir(input);
            scoItensContratoDAO.flush();
            ScoItensContrato res = input;
            
            registrarItemContratoJn(res, DominioOperacoesJournal.INS);
            
            getItensContratoDAO().desatachar(res);
            if(fsoConveniosFinanceiro!=null){
	            ScoConvItensContratoId id = new ScoConvItensContratoId(res.getSeq(), fsoConveniosFinanceiro.getCodigo());
	            ScoConvItensContrato convcont = new ScoConvItensContrato();
	            convcont.setValorRateio(input.getVlTotal());
	            convcont.setId(id);
	            convcont.setFsoConveniosFinanceiro(fsoConveniosFinanceiro);
	            scoConvItensContratoDAO.persistir(convcont);
	            scoConvItensContratoDAO.flush();
	            ScoConvItensContrato result = convcont;
	            scoConvItensContratoDAO.desatachar(result);
	            List l = new ArrayList<ScoConvItensContrato>();
	            l.add(result);
	            res.setConvItensContrato(l);
            }
            return res;
        }          
    }
	
	private boolean excessaoPorTamanhoUnidade(ScoItensContrato input){
		boolean excessao = false;
		
		if (input.getUnidade() != null && input.getUnidade().length() > 20){
			excessao = true;
		} else {
			excessao = false;
		}
		
		return excessao;
	}
	
	
	/**
	 * Excluir.
	 *
	 * @param itensContrato the itens contrato
	 *  the aGHU negocio exception
	 */
	public void excluir(Integer itemSeq) throws ApplicationBusinessException {
		ScoItensContratoDAO scoItensContratoDAO = this.getItensContratoDAO();
		ScoConvItensContratoDAO scoConvItensContratoDAO = this.getConvItensContratoDAO();
		
		ScoItensContrato itensContrato = scoItensContratoDAO.obterPorChavePrimaria(itemSeq);
		
		if(itensContrato==null) {
			return;
		}
    	//RN01
    	if(!verificaRN01OK(itensContrato)) {
    		throw new ApplicationBusinessException(ManterItensContratoONExceptionCode.CONTRATO_JA_ENVIADA_E_CONTRATO_TEM_ADITIVOS);
    	}
    	
		List<ScoConvItensContrato> res = scoConvItensContratoDAO.getItensContratoConvenioByContrato(itensContrato);
		for(ScoConvItensContrato it: res){
			scoConvItensContratoDAO.remover(it);
			scoConvItensContratoDAO.flush();
		}
		
		scoItensContratoDAO.removerPorId(itemSeq);
		
		registrarItemContratoJn(itensContrato, DominioOperacoesJournal.DEL);
	}	
	
	public void registrarItemContratoJn(ScoItensContrato item, DominioOperacoesJournal operacao){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoItensContratoJn itemJn = BaseJournalFactory.getBaseJournal(operacao,ScoItensContratoJn.class, servidorLogado.getUsuario());
		
		itemJn.setSeq(item.getSeq());
		itemJn.setMarcaComercial(item.getMarcaComercial());
		itemJn.setContrato(item.getContrato());
		itemJn.setNrItem(item.getNrItem());
		itemJn.setVersion(item.getVersion());
		itemJn.setMaterial(item.getMaterial());
		itemJn.setServico(item.getServico());
		itemJn.setQuantidade(item.getQuantidade());
		itemJn.setUnidade(item.getUnidade());
		itemJn.setVlUnitario(item.getVlUnitario());
		itemJn.setDescricao(item.getDescricao());

		getScoItensContratoJnDAO().persistir(itemJn);
		getScoItensContratoJnDAO().flush();		
	}
	

	/**
	 * Checks if is atualizacao.
	 *
	 * @param input the input
	 * @return true, if is atualizacao
	 */
	private boolean isAtualizacao(ScoItensContrato input) {
		return (input.getSeq()==null) ? false : true;
	}

	/**
	 * Propriedades obrigatorias preenchidas.
	 *
	 * @param input the input
	 */
	private void propriedadesObrigatoriasPreenchidas(ScoItensContrato input) {
		// TODO Auto-generated method stub
		
	
	}
	
	/**
	 * Verifica r n01 ok.
	 * Se o contrato já estiver sido enviado com sucesso ao SICON e possuir aditivos, 
	 * o sistema não deve permitir a inserção de novo itens, edição e exclusão dos itens já existentes.
	 * @param input the input
	 * @return true, if successful
	 */
	private boolean verificaRN01OK(ScoItensContrato input){
    	if((input.getContrato().getSituacao() == DominioSituacaoEnvioContrato.E)
    			&& getScoAditContratoDAO().obterAditivosByContrato(input.getContrato()).size()>0) {
    		return false;
    	} else {
    		return true;
    	}
	}
    
	/**
	 * Gets the dao.
	 *
	 * @return the dao
	 */
	protected ScoItensContratoDAO getItensContratoDAO() {
		if(dao!=null) {
			return dao;
		} else {
			dao = scoItensContratoDAO;
		}
		return dao;
	}
	
	/**
	 * Gets the conv itens contrato dao.
	 *
	 * @return the conv itens contrato dao
	 */
	protected ScoConvItensContratoDAO getConvItensContratoDAO() {
		if(convItensContratoDAO!=null) {
			return convItensContratoDAO;
		} else {
			convItensContratoDAO = scoConvItensContratoDAO;
		}
		return convItensContratoDAO;
	}
	
	/**
	 * Gets the sco adit contrato dao.
	 *
	 * @return the sco adit contrato dao
	 */
	protected ScoAditContratoDAO getScoAditContratoDAO(){
		if(scoAditContratoDAO!=null) {
			return scoAditContratoDAO;
		} else {
			scoAditContratoDAO = scoAditContratoDAO;
		}
		return scoAditContratoDAO;
	}
	
	protected ScoItensContratoJnDAO getScoItensContratoJnDAO(){
		return scoItensContratoJnDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}