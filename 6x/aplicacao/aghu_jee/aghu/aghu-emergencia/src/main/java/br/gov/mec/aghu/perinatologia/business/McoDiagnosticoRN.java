package br.gov.mec.aghu.perinatologia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.CidVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoFiltro;
import br.gov.mec.aghu.emergencia.vo.DiagnosticoVO;
import br.gov.mec.aghu.model.McoDiagnostico;
import br.gov.mec.aghu.model.McoDiagnosticoJn;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoDiagnosticoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoDiagnosticoJnDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.service.ServiceException;

/**
 * Regras de negócio relacionadas à entidade McoDiagnostico
 * 
 * @author felipe rocha
 * 
 */
@Stateless
public class McoDiagnosticoRN extends BaseBusiness {
	private static final long serialVersionUID = 1357278607603261830L;


	@Inject
	private McoDiagnosticoDAO mcoDiagnosticoDAO;

	@Inject
	private McoDiagnosticoJnDAO mcoDiagnosticoJnDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private IConfiguracaoService configuracaoService;
	
	@Inject
	private RapServidoresDAO servidorDAO;


	
	public enum McoDiagnosticoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL;
	}
	
	
	/**
	 * @ORADB TRIGGER "AGH". MCOT_DIG_BRI BEFORE INSERT ON MCO_DIAGNOSTICOS
	 *
	 * Insere um registro de McoDiagnostico
	 *
	 * #26109 - RN01
	 * 
	 * @param  diagnostico
	 * @throws ApplicationBusinessException
	 */
	private void inserir(McoDiagnostico diagnostico) throws ApplicationBusinessException {
		
		if (diagnostico.getIndPlacar() == null){
			diagnostico.setIndPlacar(false);
		}
		diagnostico.setCriadoEm(new Date());
		this.mcoDiagnosticoDAO.persistir(diagnostico);
	}
	
	
	/**
	 *	Persiste um registro de McoDiagnostico
	 *
	 * #26109 - RN02 e RN 04
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	public void persistir(McoDiagnostico diagnostico) throws ApplicationBusinessException {
				
		diagnostico.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		if (diagnostico.getSeq() == null) {
			inserir(diagnostico);
		} else {
			atualizar(diagnostico);
		}
	}
	
	/**
	 *	@ORADB TRIGGER "AGH".MCOT_DIG_BRU BEFORE UPDATE OF MCO_DIAGNOSTICOS
	 *
	 * Atualiza um registro de McoDiagnostico
	 *
	 * #26109 - RN03
	 * 
	 * @param  diagnostico
	 * @throws ApplicationBusinessException
	 */
	private void atualizar(McoDiagnostico diagnostico) throws ApplicationBusinessException {
		McoDiagnostico diagnosticoOriginal = this.mcoDiagnosticoDAO.obterOriginal(diagnostico);		
		if (atualizaJornal(diagnostico, diagnosticoOriginal)) {
			posUpdate(diagnosticoOriginal);
		}
		
		this.mcoDiagnosticoDAO.atualizar(diagnostico);
	
	}


	
	/**
	 * Ativa/Inativa um registro de McoDiagnostico
	 * 
	 *  #26109 - RN06
	 * 
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativar(McoDiagnostico diagnostico) throws ApplicationBusinessException {
		// 1. Se ind_situacao = ‘A’, seta ‘I’ e vice-versa
		DominioSituacao situacao = DominioSituacao.A.equals(diagnostico.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A;
		diagnostico.setIndSituacao(situacao);
		// 2. Realizar update U1
		atualizar(diagnostico);

	}
	

	/**
	 * Pos-Update de McoDiagnostico
	 * 
	 * #32283 - RN05 
	 * 
	 * @param McoDiagnostico
	 */
	private void posUpdate(McoDiagnostico diagnostico) {
			this.inserirJournal(diagnostico, DominioOperacoesJournal.UPD);
	}
	
	private boolean atualizaJornal(McoDiagnostico diagnostico, McoDiagnostico diagnosticoOriginal){
		if (CoreUtil.modificados(diagnosticoOriginal.getIndPlacar(),diagnostico.getIndPlacar())
				||CoreUtil.modificados(diagnosticoOriginal.getIndSituacao(),diagnostico.getIndSituacao()) 
				||CoreUtil.modificados(diagnosticoOriginal.getAghCid() ,diagnostico.getAghCid())){
			
			return true;
		}
		return false;
	}

	
	/**
	 * Insere um registro na journal de MamOrigemPaciente
	 * 
	 * @param mamOrigemPaciente
	 * @param operacao
	 */
	private void inserirJournal(McoDiagnostico diagnosticoOrigem, DominioOperacoesJournal operacao) {
		McoDiagnosticoJn jn = BaseJournalFactory.getBaseJournal(operacao, McoDiagnosticoJn.class,  usuario.getLogin());
		jn.setCriadoEm(diagnosticoOrigem.getCriadoEm());
		
		if (diagnosticoOrigem.getAghCid() != null) {
			jn.setCidSeq(diagnosticoOrigem.getAghCid().getSeq());
		}
		
		jn.setDescricao(diagnosticoOrigem.getDescricao());
		jn.setIndPlacar(diagnosticoOrigem.getIndPlacar());
		jn.setIndSituacao(diagnosticoOrigem.getIndSituacao());
		jn.setCriadoEm(diagnosticoOrigem.getCriadoEm());
		jn.setSeq(diagnosticoOrigem.getSeq());
		jn.setOperacao(operacao);
		jn.setSerMatricula(diagnosticoOrigem.getRapServidores().getId().getMatricula());
		jn.setSerVinCodigo(diagnosticoOrigem.getRapServidores().getId().getVinCodigo());
		this.mcoDiagnosticoJnDAO.persistir(jn);
	}
	
	
	
	private List<CidVO> listarCids(Integer cid) throws ApplicationBusinessException {
		List<CidVO> result = null;
		
		try {
			List<Integer>  listaCid = new ArrayList<Integer>();
			listaCid.add(cid);
			result = this.configuracaoService.pesquisarCidPorSeq(listaCid);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoDiagnosticoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	/**
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<DiagnosticoVO> listarDiagnosticos(Integer firstResult, Integer maxResults, String orderProperty,boolean asc, DiagnosticoFiltro filtro) throws ApplicationBusinessException{
		List<McoDiagnostico> listaDiagnostico = new ArrayList<McoDiagnostico>();
		List<DiagnosticoVO> listaVO  = new ArrayList<DiagnosticoVO>();
		listaDiagnostico = this.mcoDiagnosticoDAO.pesquisarDiagnostico(firstResult, maxResults, orderProperty, asc, filtro);

		if (listaDiagnostico  != null && !listaDiagnostico.isEmpty()) {
			for (McoDiagnostico diagnostico : listaDiagnostico) {
				DiagnosticoVO vo = new DiagnosticoVO();
				vo.setSeq(diagnostico.getSeq());
				vo.setDescricao(diagnostico.getDescricao());
				vo.setIndPlacar(diagnostico.getIndPlacar());
				vo.setIndSituacao(diagnostico.getIndSituacao());
				
				if (diagnostico.getAghCid() != null) {
					vo.setCidCodigo(getCidCodigo(diagnostico.getAghCid().getSeq()));
					vo.setCidSeq(diagnostico.getAghCid().getSeq());
				}
				
				listaVO.add(vo);
			}
		}
		return listaVO;
		}

	

	
	/**
	 * Retorna a codigo de um CID
	 * @param cidSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private String getCidCodigo(Integer cidSeq) throws ApplicationBusinessException{
		List<CidVO> cids = listarCids(cidSeq);
		if (cids != null &&  cids.size() > 0  && cids.get(0) != null) {
			return cids.get(0).getCodigo();
		}
		return null;
	}
	
	
	/**
	 * Retorna CIDVO
	 * @param cidSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public CidVO getCid(Integer cidSeq) throws ApplicationBusinessException{
		List<CidVO> cids = listarCids(cidSeq);
		if (cids != null &&  cids.size() > 0  && cids.get(0) != null) {
			return cids.get(0);
		}
		return null;
	}
	
	
	
	public List<CidVO> obterCids(final String param) throws ApplicationBusinessException {
		List<CidVO> result = null;
		try {
			result = this.configuracaoService.pesquisarCidPorCodigoDescricao(param);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoDiagnosticoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	
	public Long obterCidsCount(final String param) throws ApplicationBusinessException {
		Long count = null;
		try {
			count = this.configuracaoService.pesquisarCidPorCodigoDescricaoCount(param);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(McoDiagnosticoRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return count;
	}


	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}


}