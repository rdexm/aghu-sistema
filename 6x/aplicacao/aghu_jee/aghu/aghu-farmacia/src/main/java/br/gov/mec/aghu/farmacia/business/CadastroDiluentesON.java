package br.gov.mec.aghu.farmacia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaVinculoDiluentesDAO;
import br.gov.mec.aghu.farmacia.dao.AfaVinculoDiluentesJnDAO;
import br.gov.mec.aghu.farmacia.vo.CadastroDiluentesJnVO;
import br.gov.mec.aghu.farmacia.vo.CadastroDiluentesVO;
import br.gov.mec.aghu.farmacia.vo.DiluentesVO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaVinculoDiluentes;
import br.gov.mec.aghu.model.AfaVinculoDiluentesJn;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.view.VAfaDescrMdto;

@Stateless
public class CadastroDiluentesON extends BaseBusiness {

	private static final long serialVersionUID = 3916936881483760903L;
	
	private static final Log LOG = LogFactory.getLog(CadastroDiluentesON.class);

	@Override
	protected Log getLogger() {
	  return LOG;
	}
	
private static final String  DILUENTE_NULO = "Diluente não pode ser nulo." ;
	
	protected enum DiluentesExceptionCode implements BusinessExceptionCode {
		AFA_DILUENTES_NAO_SELECIONADO,
		AFA_DILUENTES_SERVIDOR_NAO_ENCONTRADO,
		AFA_DILUENTES_USUAL_PRESCRICAO_EXISTENTE,
		AFA_DILUENTES_EXISTENTE_OU_POSSUE_USUAL_PRESCRICAO,
		NAO_FOI_POSSIVEL_ATUALIZAR_MEDICAMENTO,
		NAO_FOI_POSSIVEL_ATUALIZAR_MEDICAMENTO_VINCULO_DILUENTE_ATIVO,
		AFA_DILUENTES_NAO_EXCLUIDO;
	}
	
	@Inject
	private AfaVinculoDiluentesDAO afaVinculoDiluentesDAO;
	
	@Inject
	private AfaVinculoDiluentesJnDAO afaVinculoDiluentesJnDAO;
	
	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@EJB 
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private FarmaciaON farmaciaON;
	
	
	public Long pesquisarDiluentesCount(AfaMedicamento medicamento) {
		
		if (medicamento == null) {
			throw new IllegalArgumentException(DILUENTE_NULO);
			}
		return this.getAfaVinculoDiluentesDAO().pesquisarDiluentesCount(medicamento);
	}
		
	public List<CadastroDiluentesVO> recuperarListaPaginadaDiluentes(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AfaMedicamento medicamento) {
		
		List<AfaVinculoDiluentes> listaDiluentes = getAfaVinculoDiluentesDAO().recuperarListaPaginadaDiluentes(firstResult, maxResult, orderProperty, asc, medicamento);
		List<CadastroDiluentesVO> listaDiluentesVO = new ArrayList<CadastroDiluentesVO>();
		populaVinculoDiluentesVO(listaDiluentes, listaDiluentesVO);
		
		return listaDiluentesVO;
	}

	public void populaVinculoDiluentesVO(List<AfaVinculoDiluentes> listaDiluentes,	List<CadastroDiluentesVO> listaDiluentesVO) {
		CadastroDiluentesVO diluenteVO;
		for(AfaVinculoDiluentes vinculoDiluente : listaDiluentes) {
			
			diluenteVO =  new CadastroDiluentesVO();
			diluenteVO.setSeq(vinculoDiluente.getSeq());
			diluenteVO.setDescricao(vinculoDiluente.getDiluente().getDescricao());
			diluenteVO.setUsualPrescricao(vinculoDiluente.getIndUsualPrescricao());
			diluenteVO.setSituacao(vinculoDiluente.getIndSituacao().getDescricao());
			diluenteVO.setSeqMedicamento(vinculoDiluente.getMedicamento().getMatCodigo().toString());
			listaDiluentesVO.add(diluenteVO);
		}
	}
	
	public List<CadastroDiluentesJnVO> pesquisarVinculoDiluentesJn(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer codigoMedicamento) {
		List<AfaVinculoDiluentesJn> listaVinculoDiluentesJN = getAfaVinculoDiluentesJnDAO().pesquisarVinculoDiluentesJn(firstResult, maxResult, orderProperty, asc, codigoMedicamento);
		List<CadastroDiluentesJnVO> listaVinculoDiluentesJnVO = new ArrayList<CadastroDiluentesJnVO>();
		populaVinculoDiluentesJnVO(listaVinculoDiluentesJN, listaVinculoDiluentesJnVO, codigoMedicamento);
		
		return listaVinculoDiluentesJnVO;
	}
	
	private void populaVinculoDiluentesJnVO(List<AfaVinculoDiluentesJn> listaVinculoDiluentesJN,List<CadastroDiluentesJnVO> listaVinculoDiluentesJnVO, Integer codigoMedicamento) {
		CadastroDiluentesJnVO diluenteJnVO;
		
		for (AfaVinculoDiluentesJn diluenteJn : listaVinculoDiluentesJN) {
		
				AfaMedicamento medicamento = getAfaMedicamentoDAO().obterMedicamentoDiluentePorCodigoMedicamento(diluenteJn.getDiluenteCodigo());
				diluenteJnVO =  new CadastroDiluentesJnVO();
				diluenteJnVO.setDescricao(medicamento.getDescricao());
				diluenteJnVO.setUsualPrescricao(diluenteJn.getIndUsualPrescricao());
				diluenteJnVO.setSituacao(diluenteJn.getIndSituacao().getDescricao());
				diluenteJnVO.setOperacao(diluenteJn.getOperacao().getDescricao());
				diluenteJnVO.setNomeUsuario(diluenteJn.getNomeUsuario());
				diluenteJnVO.setDataAlteracao(diluenteJn.getDataAlteracao());
				diluenteJnVO.setCriadoEm(diluenteJn.getCriadoEm());
				recuperaNomeResponsavel(diluenteJn);
				diluenteJnVO.setNomeResponsavel(diluenteJn.getNomeResponsavel());
				listaVinculoDiluentesJnVO.add(diluenteJnVO);
		}
	}

	private void recuperaNomeResponsavel(AfaVinculoDiluentesJn diluenteJn) {
		RapServidoresId idRapServidor = new RapServidoresId();
		idRapServidor.setMatricula(diluenteJn.getSerMatricula());
		idRapServidor.setVinCodigo(diluenteJn.getSerVinCodigo());

		RapServidores servidor = getIRegistroColaboradorFacade().obterRapServidor(idRapServidor);

		if(servidor != null){
			RapPessoasFisicas pessoa = servidor.getPessoaFisica();
			if(pessoa != null){
				diluenteJn.setNomeResponsavel(pessoa.getNome());						
			}	
		}
	}

	public void persistirVinculoDiluentes(AfaVinculoDiluentes diluente, RapServidores servidorLogado, VAfaDescrMdto diluenteSelecionado, Integer codigoMedicamentoSelecionado) throws ApplicationBusinessException  {
		Long diluenteExistente = verificaVinculoDiluenteDuplicado(codigoMedicamentoSelecionado, diluenteSelecionado);
		
		if (diluente.getSeq() == null) {
			if ((diluenteExistente == 0) && (!verificaUsualPrescricao(diluente))) {
				preInserirAfaVinculoDiluentes(diluente, servidorLogado, diluenteSelecionado);
				getAfaVinculoDiluentesDAO().persistir(diluente);
				getAfaVinculoDiluentesDAO().flush();
			} else {
				throw new ApplicationBusinessException(DiluentesExceptionCode.AFA_DILUENTES_EXISTENTE_OU_POSSUE_USUAL_PRESCRICAO);
			}
		} else {
			atualizarVinculoDiluentes(diluente, servidorLogado);
		}
	}

	public void atualizarVinculoDiluentes(AfaVinculoDiluentes diluente, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		AfaVinculoDiluentes oldDiluente = getAfaVinculoDiluentesDAO().obterOriginal(diluente);
		
		if (!verificaUsualPrescricao(diluente)) {
			verificarServidorLogado(diluente, servidorLogado);
			diluente.setCriadoEm(new Date());
			getAfaVinculoDiluentesDAO().atualizar(diluente);
			getAfaVinculoDiluentesDAO().flush();	
			posAtualizarDiluentes(diluente, oldDiluente, servidorLogado);
		} else {
			throw new ApplicationBusinessException(DiluentesExceptionCode.AFA_DILUENTES_USUAL_PRESCRICAO_EXISTENTE);
		}
	}

	private void posAtualizarDiluentes(AfaVinculoDiluentes diluente, AfaVinculoDiluentes oldDiluente, RapServidores servidorLogado) {
		
		if (comparaDiluentesParaUpdateJn(diluente, oldDiluente)){
			
			AfaVinculoDiluentesJn diluenteJN = new BaseJournalFactory().getBaseJournal(DominioOperacoesJournal.UPD, AfaVinculoDiluentesJn.class, servidorLogado.getUsuario()) ;    
			persistirDiluentesJn(oldDiluente, diluenteJN);
			
			getAfaVinculoDiluentesJnDAO().persistir(diluenteJN);
			getAfaVinculoDiluentesJnDAO().flush();
			
		}
		
	}

	private void persistirDiluentesJn(AfaVinculoDiluentes oldDiluente,	AfaVinculoDiluentesJn diluenteJN) {
		
		diluenteJN.setSeq(oldDiluente.getSeq());
		diluenteJN.setMatCodigo(oldDiluente.getMedicamento().getMatCodigo());
		diluenteJN.setDiluenteCodigo(oldDiluente.getDiluente().getMatCodigo());
		diluenteJN.setIndUsualPrescricao(oldDiluente.getIndUsualPrescricao());
		diluenteJN.setIndSituacao(oldDiluente.getIndSituacao());
		diluenteJN.setCriadoEm(oldDiluente.getCriadoEm());
		diluenteJN.setSerMatricula(oldDiluente.getServidor() != null ? oldDiluente.getServidor().getId().getMatricula() : null);
		diluenteJN.setSerVinCodigo(oldDiluente.getServidor() != null ? oldDiluente.getServidor().getId().getVinCodigo() : null);
	}

	private boolean comparaDiluentesParaUpdateJn(AfaVinculoDiluentes diluente,	AfaVinculoDiluentes oldDiluente) {
		
		return CoreUtil.modificados(diluente.getSeq(), oldDiluente.getSeq())
				|| CoreUtil.modificados(diluente.getMedicamento(), oldDiluente.getMedicamento())
				|| CoreUtil.modificados(diluente.getDiluente(), oldDiluente.getDiluente())
				|| CoreUtil.modificados(diluente.getIndUsualPrescricao(), oldDiluente.getIndUsualPrescricao())
				|| CoreUtil.modificados(diluente.getIndSituacao(), oldDiluente.getIndSituacao())
				|| CoreUtil.modificados(diluente.getCriadoEm(), oldDiluente.getCriadoEm())
				|| CoreUtil.modificados(diluente.getServidor().getId().getMatricula(), oldDiluente.getServidor().getId().getMatricula())
				|| CoreUtil.modificados(diluente.getServidor().getId().getVinCodigo(), oldDiluente.getServidor().getId().getVinCodigo());
	}

	public Long verificaVinculoDiluenteDuplicado(Integer codigoMedicamento , VAfaDescrMdto diluente) throws ApplicationBusinessException {
		
		if (codigoMedicamento != null && diluente != null) {
			AfaMedicamento medicamento = recuperarMedicamentoDiluente(codigoMedicamento);
			return getAfaVinculoDiluentesDAO().recuperarVinculoDiluente(medicamento , diluente.getMedicamento());
		} else {
			throw new ApplicationBusinessException(DiluentesExceptionCode.AFA_DILUENTES_NAO_SELECIONADO);
		}
	}

	public void preInserirAfaVinculoDiluentes(AfaVinculoDiluentes diluente, RapServidores servidorLogado, VAfaDescrMdto diluenteSelecionado) throws ApplicationBusinessException {
		
		if (diluenteSelecionado != null) {
			diluente.setDiluente(recuperarMedicamentoDiluente(diluenteSelecionado.getMedicamento().getMatCodigo()));
		}
		diluente.setCriadoEm(new Date());
		verificarServidorLogado(diluente, servidorLogado);
		
	}

	public void verificarServidorLogado(AfaVinculoDiluentes diluente, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		if (servidorLogado != null) {
			diluente.setServidor(servidorLogado);
		} else {
			throw new ApplicationBusinessException(DiluentesExceptionCode.AFA_DILUENTES_SERVIDOR_NAO_ENCONTRADO);
		}
	}

	public AfaMedicamento recuperarMedicamentoDiluente(Integer codDiluenteSelecionado) {
		
		return this.getAfaMedicamentoDAO().obterMedicamentoDiluentePorCodigoMedicamento(codDiluenteSelecionado);
	}

	public Boolean verificaUsualPrescricao(AfaVinculoDiluentes diluente) {
		
		Long result = this.getAfaVinculoDiluentesDAO().verificaUsualPrescricao(diluente);
		if ((diluente.getIndUsualPrescricao()) && (result > 0) && (diluente.getIndSituacao() == DominioSituacao.A)) {
			return Boolean.TRUE; 
		}
		return Boolean.FALSE;
	}

	public void excluirDiluente(AfaVinculoDiluentes diluente, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		if (diluente != null) {
			AfaVinculoDiluentes recuperaDiluente = getAfaVinculoDiluentesDAO().obterPorChavePrimaria(diluente.getSeq());
			getAfaVinculoDiluentesDAO().remover(recuperaDiluente);
			getAfaVinculoDiluentesDAO().flush();
			posDeleteDiluentes(recuperaDiluente, servidorLogado.getUsuario());
		} else {
			throw new ApplicationBusinessException(DiluentesExceptionCode.AFA_DILUENTES_NAO_EXCLUIDO);
		}
	}
	
	private void posDeleteDiluentes(AfaVinculoDiluentes diluente, String servidorLogado) {
		
		AfaVinculoDiluentesJn diluenteJN = new BaseJournalFactory().getBaseJournal(DominioOperacoesJournal.DEL, AfaVinculoDiluentesJn.class, servidorLogado) ;    
		persistirDiluentesJn(diluente, diluenteJN);
		getAfaVinculoDiluentesJnDAO().persistir(diluenteJN);
		getAfaVinculoDiluentesJnDAO().flush();
		
	}
	
	public Long pesquisarVinculoDiluentesJnCount(Integer codMedicamento) {
		
		if (codMedicamento == null) {
			throw new IllegalArgumentException(DILUENTE_NULO);
			}
		return this.getAfaVinculoDiluentesJnDAO().pesquisarVinculoDiluentesJnCount(codMedicamento);
	}
	
	public List<DiluentesVO> recuperaListaVinculoDiluente(String descricaoMedicamento, AfaMedicamento medicamento) {
		List<DiluentesVO> diluenteVO = new ArrayList<DiluentesVO>();
		List<AfaVinculoDiluentes> diluentes = getAfaVinculoDiluentesDAO().recuperaListaVinculoDiluente(descricaoMedicamento, medicamento);
		
		if (diluentes != null && !diluentes.isEmpty()) {
			diluenteVO = populaVoVinculoDiluentes(diluentes);
		} else {
			diluenteVO = populaVoDiluentesVAfaDescrMdto(descricaoMedicamento);
		}
		return diluenteVO;
	}
		
	public List<DiluentesVO>  populaVoVinculoDiluentes(List<AfaVinculoDiluentes> diluentes) {
		List<DiluentesVO> listaDiluentesVO = new ArrayList<DiluentesVO>();
		
		for (AfaVinculoDiluentes diluente : diluentes) {
			DiluentesVO diluenteVO = new DiluentesVO();
			diluenteVO.setSeqMedicamento(diluente.getMedicamento().getMatCodigo().toString());
			diluenteVO.setSeqDiluente(diluente.getDiluente().getMatCodigo());
			diluenteVO.setDescricao(diluente.getDiluente().getDescricao().toString());
			diluenteVO.setUsualPrescricao(diluente.getIndUsualPrescricao());
			diluenteVO.setSituacao(diluente.getIndSituacao().getDescricao());
			listaDiluentesVO.add(diluenteVO);	
		}
		return listaDiluentesVO;
	}

	public List<DiluentesVO> populaVoDiluentesVAfaDescrMdto(String descricaoMedicamento) {
		List<VAfaDescrMdto> listaVafaDescrMdto = getFarmaciaON().obterListaDiluentes(descricaoMedicamento);
		List<DiluentesVO> listaDiluentesVO = new ArrayList<DiluentesVO>();
		
		for (VAfaDescrMdto vafaDescrMdto : listaVafaDescrMdto) {
			DiluentesVO diluenteVO = new DiluentesVO();
			
			diluenteVO.setSeqDiluente(vafaDescrMdto.getMedicamento().getMatCodigo());
			diluenteVO.setTipoUsoMdto(vafaDescrMdto.getTipoUsoMdto().getSigla());
			diluenteVO.setDescricao(vafaDescrMdto.getMedicamento().getDescricao());
			listaDiluentesVO.add(diluenteVO);
			
		}
		return listaDiluentesVO;
	}
	
	public DiluentesVO popularBuscaUsualPrescricaoPorMedicamento(Integer codigoMedicamento) {
		
		AfaVinculoDiluentes diluente = getAfaVinculoDiluentesDAO().buscarUsualPrescricaoPorMedicamento(codigoMedicamento);
		DiluentesVO diluenteVO = new DiluentesVO();
		
		if (diluente != null) {
			diluenteVO.setSeqMedicamento(diluente.getMedicamento().getMatCodigo().toString());
			diluenteVO.setSeqDiluente(diluente.getDiluente().getMatCodigo());
			diluenteVO.setDescricao(diluente.getDiluente().getDescricao().toString());
			diluenteVO.setUsualPrescricao(diluente.getIndUsualPrescricao());
			diluenteVO.setSituacao(diluente.getIndSituacao().getDescricao());
		}
		
		return diluenteVO;
	}
	
	public void verificarVinculoDiluenteExistente(AfaMedicamento medicamento, Boolean indDiluente, DominioSituacaoMedicamento situacao) throws ApplicationBusinessException {
		Boolean possuiVinculoDiluente = afaVinculoDiluentesDAO.verificarVinculoDiluenteExistente(medicamento);
		
		if(possuiVinculoDiluente && !indDiluente) {
			throw new ApplicationBusinessException(DiluentesExceptionCode.NAO_FOI_POSSIVEL_ATUALIZAR_MEDICAMENTO);
		} else {
			if(possuiVinculoDiluente && situacao != DominioSituacaoMedicamento.A) {
				throw new ApplicationBusinessException(DiluentesExceptionCode.NAO_FOI_POSSIVEL_ATUALIZAR_MEDICAMENTO_VINCULO_DILUENTE_ATIVO);
			}
		}
	}


	public AfaVinculoDiluentes obterDiluente(Integer codigoDiluente) {
		return afaVinculoDiluentesDAO.obterPorChavePrimaria(codigoDiluente);
	}
	
	protected AfaVinculoDiluentesDAO getAfaVinculoDiluentesDAO() {
		return afaVinculoDiluentesDAO;
	}
	
	protected AfaVinculoDiluentesJnDAO getAfaVinculoDiluentesJnDAO() {
		return afaVinculoDiluentesJnDAO;
	}
	
	protected FarmaciaON getFarmaciaON() {
		return farmaciaON;
	}

	protected AfaMedicamentoDAO getAfaMedicamentoDAO() {
		return afaMedicamentoDAO;
	}
	
	protected IRegistroColaboradorFacade getIRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
}
