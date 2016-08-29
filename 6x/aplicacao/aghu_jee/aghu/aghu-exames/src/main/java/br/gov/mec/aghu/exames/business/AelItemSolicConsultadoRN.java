package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelItemSolicConsultadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicConsultadoHistDAO;
import br.gov.mec.aghu.exames.vo.AelItemSolicConsultadoVO;
import br.gov.mec.aghu.model.AelItemSolicConsultado;
import br.gov.mec.aghu.model.AelItemSolicConsultadoHist;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lcmoura
 * 
 */
@Stateless
public class AelItemSolicConsultadoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelItemSolicConsultadoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemSolicConsultadoHistDAO aelItemSolicConsultadoHistDAO;
	
	@Inject
	private AelItemSolicConsultadoDAO aelItemSolicConsultadoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private static final long serialVersionUID = -629759624714950148L;

	public List<AelItemSolicConsultadoVO> pesquisarAelItemSolicConsultadosResultadosExames(
			Integer iseSoeSeq, Short iseSeqp) {

		List<AelItemSolicConsultado> resultadoPesquisa = getAelItemSolicConsultadoDAO()
				.pesquisarAelItemSolicConsultadosResultadosExames(iseSoeSeq,
						iseSeqp);

		List<AelItemSolicConsultadoVO> listaRetornoVos = null;

		if (resultadoPesquisa != null && !resultadoPesquisa.isEmpty()) {
			listaRetornoVos = new LinkedList<AelItemSolicConsultadoVO>();

			for (AelItemSolicConsultado item : resultadoPesquisa) {

				AelItemSolicConsultadoVO vo = new AelItemSolicConsultadoVO();
				RapServidoresId id = new RapServidoresId();
				id.setMatricula(item.getId().getSerMatricula());
				id.setVinCodigo(item.getId().getSerVinCodigo());
				RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(id);
				vo.setServidor(servidor.getPessoaFisica().getNome());
				vo.setCriadoEm(item.getId().getCriadoEm());
				vo.setMatricula(item.getId().getSerMatricula());
				vo.setVinculo(item.getId().getSerVinCodigo());

				listaRetornoVos.add(vo);
			}
			return listaRetornoVos;
		}
		return null;
	}

	public List<AelItemSolicConsultadoVO> pesquisarAelItemSolicConsultadosResultadosExamesHist(
			Integer iseSoeSeq, Short iseSeqp) {

		List<AelItemSolicConsultadoHist> resultadoPesquisa = getAelItemSolicConsultadoHistDAO()
				.pesquisarAelItemSolicConsultadosResultadosExamesHist(iseSoeSeq,
						iseSeqp);

		List<AelItemSolicConsultadoVO> listaRetornoVos = null;

		if (resultadoPesquisa != null && !resultadoPesquisa.isEmpty()) {
			listaRetornoVos = new LinkedList<AelItemSolicConsultadoVO>();

			for (AelItemSolicConsultadoHist item : resultadoPesquisa) {

				AelItemSolicConsultadoVO vo = new AelItemSolicConsultadoVO();
				RapServidoresId id = new RapServidoresId();
				id.setMatricula(item.getId().getSerMatricula());
				id.setVinCodigo(item.getId().getSerVinCodigo());
				RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(id);
				vo.setServidor(servidor.getPessoaFisica().getNome());
				vo.setCriadoEm(item.getId().getCriadoEm());
				vo.setMatricula(item.getId().getSerMatricula());
				vo.setVinculo(item.getId().getSerVinCodigo());

				listaRetornoVos.add(vo);
			}
			return listaRetornoVos;
		}
		return null;
	}

	/**
	 * Efetua o insert de um item de solicitação consultado
	 * 
	 * @HIST AelItemSolicConsultadoRN.inserirHist
	 * @param itemSolicConsultado
	 * @throws ApplicationBusinessException 
	 */
	public void inserir(AelItemSolicConsultado itemSolicConsultado, Boolean flush) throws ApplicationBusinessException{
		preInsert(itemSolicConsultado);
		getAelItemSolicConsultadoDAO().persistir(itemSolicConsultado);
		if (flush) {
			getAelItemSolicConsultadoDAO().flush();
		}
	}
	
	public void inserirHist(AelItemSolicConsultadoHist itemSolicConsultado, Boolean flush) throws ApplicationBusinessException{
		preInsertHist(itemSolicConsultado);
		getAelItemSolicConsultadoHistDAO().persistir(itemSolicConsultado);
		if (flush) {
			getAelItemSolicConsultadoHistDAO().flush();
		}
	}

	/**
	 * @HIST AelItemSolicConsultadoRN.preInsertHist
	 * @param itemSolicConsultado
	 * @throws ApplicationBusinessException 
	 */
	private void preInsert(AelItemSolicConsultado itemSolicConsultado) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		itemSolicConsultado.getId().setCriadoEm(new Date());
		
		if (itemSolicConsultado.getId().getSerMatricula() == null
				|| itemSolicConsultado.getId().getSerVinCodigo() == null) {
			itemSolicConsultado.getId().setSerMatricula(
					servidorLogado.getId().getMatricula());
			itemSolicConsultado.getId().setSerVinCodigo(
					servidorLogado.getId().getVinCodigo());
		}
	}
	
	private void preInsertHist(AelItemSolicConsultadoHist itemSolicConsultado) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		itemSolicConsultado.getId().setCriadoEm(new Date());
		
		if (itemSolicConsultado.getId().getSerMatricula() == null
				|| itemSolicConsultado.getId().getSerVinCodigo() == null) {
		
			RapServidores servidor=null;
			try {
				servidor = servidorLogado;
			} catch (Exception e) {
				logError("Não encontrou o servidor logado!");
			}

			itemSolicConsultado.getId().setSerMatricula(servidor.getId().getMatricula());
			itemSolicConsultado.getId().setSerVinCodigo(servidor.getId().getVinCodigo());
		}
	}

	
	protected AelItemSolicConsultadoDAO getAelItemSolicConsultadoDAO() {
		return aelItemSolicConsultadoDAO;
	}
	
	protected AelItemSolicConsultadoHistDAO getAelItemSolicConsultadoHistDAO() {
		return aelItemSolicConsultadoHistDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
