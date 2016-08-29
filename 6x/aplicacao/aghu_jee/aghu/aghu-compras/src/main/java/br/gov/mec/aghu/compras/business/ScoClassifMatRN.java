package br.gov.mec.aghu.compras.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv1DAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv1JnDAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv2DAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv2JnDAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv3DAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv3JnDAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv4DAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv4JnDAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv5DAO;
import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv5JnDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoClassifMatNiv1;
import br.gov.mec.aghu.model.ScoClassifMatNiv1Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv1Jn;
import br.gov.mec.aghu.model.ScoClassifMatNiv2;
import br.gov.mec.aghu.model.ScoClassifMatNiv2Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv2Jn;
import br.gov.mec.aghu.model.ScoClassifMatNiv3;
import br.gov.mec.aghu.model.ScoClassifMatNiv3Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv3Jn;
import br.gov.mec.aghu.model.ScoClassifMatNiv4;
import br.gov.mec.aghu.model.ScoClassifMatNiv4Id;
import br.gov.mec.aghu.model.ScoClassifMatNiv4Jn;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.ScoClassifMatNiv5Jn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * RN de {@link ScoClassifMatNiv1}, {@link ScoClassifMatNiv2}, {@link ScoClassifMatNiv3}, {@link ScoClassifMatNiv4}, {@link ScoClassifMatNiv5}
 * 
 * @author luismoura
 * 
 */
@Stateless
public  class ScoClassifMatRN extends BaseBusiness {
	private static final long serialVersionUID = 4855353816484169453L;

	public enum ScoClassifMatRNExceptionCode implements BusinessExceptionCode {
		M1_CLASSIFICACAO_MATERIAL, //
		M2_CLASSIFICACAO_MATERIAL, //
		M3_CLASSIFICACAO_MATERIAL, //
		M4_CLASSIFICACAO_MATERIAL, //
		M5_CLASSIFICACAO_MATERIAL, //
		M6_CLASSIFICACAO_MATERIAL, //
		M7_CLASSIFICACAO_MATERIAL, //
		M8_CLASSIFICACAO_MATERIAL, //
		M9_CLASSIFICACAO_MATERIAL, //
		;
	}
	
	private static final Log LOG = LogFactory.getLog(ScoClassifMatRN.class);
	
	@Inject
	private ScoClassifMatNiv1DAO scoClassifMatNiv1DAO;
	
	@Inject
	private ScoClassifMatNiv2DAO scoClassifMatNiv2DAO;
	
	@Inject
	private ScoClassifMatNiv3DAO scoClassifMatNiv3DAO;
	
	@Inject
	private ScoClassifMatNiv4DAO scoClassifMatNiv4DAO;
	
	@Inject
	private ScoClassifMatNiv5DAO scoClassifMatNiv5DAO;
	
	@Inject
	private ScoClassifMatNiv1JnDAO scoClassifMatNiv1JnDAO;
	
	@Inject
	private ScoClassifMatNiv2JnDAO scoClassifMatNiv2JnDAO;
	
	@Inject
	private ScoClassifMatNiv3JnDAO scoClassifMatNiv3JnDAO;
	
	@Inject
	private ScoClassifMatNiv4JnDAO scoClassifMatNiv4JnDAO;
	
	@Inject
	private ScoClassifMatNiv5JnDAO scoClassifMatNiv5JnDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private ScoClassifMatNiv1 persistirScoClassifMatNiv1(Integer gmtCodigo, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv1Id scoClassifMatNiv1Id = new ScoClassifMatNiv1Id();
		scoClassifMatNiv1Id.setGmtCodigo(gmtCodigo);
		ScoClassifMatNiv1 scoClassifMatNiv1 = new ScoClassifMatNiv1();
		scoClassifMatNiv1.setId(scoClassifMatNiv1Id);
		scoClassifMatNiv1.setDescricao(descricao);
		scoClassifMatNiv1.setSerMatricula(servidorLogado.getId().getMatricula());
		scoClassifMatNiv1.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		scoClassifMatNiv1.setSerMatriculaAlterado(null);
		scoClassifMatNiv1.setSerVinCodigoAlterado(null);
		scoClassifMatNiv1.setCriadoEm(new Date());
		getScoClassifMatNiv1DAO().persistir(scoClassifMatNiv1);
		return scoClassifMatNiv1;
	}

	private ScoClassifMatNiv2 persistirScoClassifMatNiv2(Integer cn1GmtCodigo, Integer cn1Codigo, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv2Id scoClassifMatNiv2Id = new ScoClassifMatNiv2Id();
		scoClassifMatNiv2Id.setCn1GmtCodigo(cn1GmtCodigo);
		scoClassifMatNiv2Id.setCn1Codigo(cn1Codigo);
		ScoClassifMatNiv2 scoClassifMatNiv2 = new ScoClassifMatNiv2();
		scoClassifMatNiv2.setId(scoClassifMatNiv2Id);
		scoClassifMatNiv2.setDescricao(descricao);
		scoClassifMatNiv2.setSerMatricula(servidorLogado.getId().getMatricula());
		scoClassifMatNiv2.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		scoClassifMatNiv2.setSerMatriculaAlterado(null);
		scoClassifMatNiv2.setSerVinCodigoAlterado(null);
		scoClassifMatNiv2.setCriadoEm(new Date());
		getScoClassifMatNiv2DAO().persistir(scoClassifMatNiv2);
		return scoClassifMatNiv2;
	}

	private ScoClassifMatNiv3 persistirScoClassifMatNiv3(Integer cn2cn1GmtCodigo, Integer cn2cn1Codigo, Integer cn2Codigo, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv3Id scoClassifMatNiv3Id = new ScoClassifMatNiv3Id();
		scoClassifMatNiv3Id.setCn2Cn1GmtCodigo(cn2cn1GmtCodigo);
		scoClassifMatNiv3Id.setCn2Cn1Codigo(cn2cn1Codigo);
		scoClassifMatNiv3Id.setCn2Codigo(cn2Codigo);
		ScoClassifMatNiv3 scoClassifMatNiv3 = new ScoClassifMatNiv3();
		scoClassifMatNiv3.setId(scoClassifMatNiv3Id);
		scoClassifMatNiv3.setDescricao(descricao);
		scoClassifMatNiv3.setSerMatricula(servidorLogado.getId().getMatricula());
		scoClassifMatNiv3.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		scoClassifMatNiv3.setSerMatriculaAlterado(null);
		scoClassifMatNiv3.setSerVinCodigoAlterado(null);
		scoClassifMatNiv3.setCriadoEm(new Date());
		getScoClassifMatNiv3DAO().persistir(scoClassifMatNiv3);
		return scoClassifMatNiv3;
	}

	private ScoClassifMatNiv4 persistirScoClassifMatNiv4(Integer cn3cn2cn1GmtCodigo, Integer cn3cn2cn1Codigo, Integer cn3cn2Codigo, Integer cn3Codigo, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv4Id scoClassifMatNiv4Id = new ScoClassifMatNiv4Id();
		scoClassifMatNiv4Id.setCn3Cn2Cn1GmtCodigo(cn3cn2cn1GmtCodigo);
		scoClassifMatNiv4Id.setCn3Cn2Cn1Codigo(cn3cn2cn1Codigo);
		scoClassifMatNiv4Id.setCn3Cn2Codigo(cn3cn2Codigo);
		scoClassifMatNiv4Id.setCn3Codigo(cn3Codigo);
		ScoClassifMatNiv4 scoClassifMatNiv4 = new ScoClassifMatNiv4();
		scoClassifMatNiv4.setId(scoClassifMatNiv4Id);
		scoClassifMatNiv4.setDescricao(descricao);
		scoClassifMatNiv4.setSerMatricula(servidorLogado.getId().getMatricula());
		scoClassifMatNiv4.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		scoClassifMatNiv4.setSerMatriculaAlterado(null);
		scoClassifMatNiv4.setSerVinCodigoAlterado(null);
		scoClassifMatNiv4.setCriadoEm(new Date());
		getScoClassifMatNiv4DAO().persistir(scoClassifMatNiv4);
		return scoClassifMatNiv4;
	}

	private ScoClassifMatNiv5 persistirScoClassifMatNiv5(ScoClassifMatNiv4 scoClassifMatNiv4, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv5 scoClassifMatNiv5 = new ScoClassifMatNiv5();
		scoClassifMatNiv5.setScoClassifMatNiv4(scoClassifMatNiv4);
		scoClassifMatNiv5.setDescricao(descricao);
		scoClassifMatNiv5.setSerMatricula(servidorLogado.getId().getMatricula());
		scoClassifMatNiv5.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		scoClassifMatNiv5.setSerMatriculaAlterado(null);
		scoClassifMatNiv5.setSerVinCodigoAlterado(null);
		scoClassifMatNiv5.setCriadoEm(new Date());
		getScoClassifMatNiv5DAO().persistir(scoClassifMatNiv5);
		return scoClassifMatNiv5;
	}

	/**
	 * RN01 de #5758
	 * 
	 * ORADB: SCOT_CN1_ASI
	 * 
	 * @param gmtCodigo
	 * @param codigo
	 * @param descricao
	 * @param servidorLogado
	 */
	public void inserirScoClassifMatNiv1(Integer gmtCodigo, String descricao) {
		// - Executar I1 e executar RN02 passando CODIGO=0 e DESCRICAO=NULL nos inserts subsequentes.
		ScoClassifMatNiv1 scoClassifMatNiv1 = this.persistirScoClassifMatNiv1(gmtCodigo, descricao);
		this.inserirScoClassifMatNiv2(gmtCodigo, scoClassifMatNiv1.getId().getCodigo(), null);
	}

	/**
	 * RN02 de #5758
	 * 
	 * ORADB: SCOT_CN2_ASI
	 * 
	 * @param cn1GmtCodigo
	 * @param cn1Codigo
	 * @param codigo
	 * @param descricao
	 * @param servidorLogado
	 */
	public void inserirScoClassifMatNiv2(Integer cn1GmtCodigo, Integer cn1Codigo, String descricao) {
		// - Executar I2 e executar RN03 passando CODIGO=0 e DESCRICAO=NULL nos inserts subsequentes.
		ScoClassifMatNiv2 scoClassifMatNiv2 = this.persistirScoClassifMatNiv2(cn1GmtCodigo, cn1Codigo, descricao);
		this.inserirScoClassifMatNiv3(cn1GmtCodigo, cn1Codigo, scoClassifMatNiv2.getId().getCodigo(), null);
	}

	/**
	 * RN03 de #5758
	 * 
	 * ORADB: SCOT_CN3_ASI
	 * 
	 * @param cn2cn1GmtCodigo
	 * @param cn2cn1Codigo
	 * @param cn2Codigo
	 * @param codigo
	 * @param descricao
	 * @param servidorLogado
	 */
	public void inserirScoClassifMatNiv3(Integer cn2cn1GmtCodigo, Integer cn2cn1Codigo, Integer cn2Codigo, String descricao) {
		// - Executar I3 e executar RN04 passando CODIGO=0 e DESCRICAO=NULL nos inserts subsequentes.
		ScoClassifMatNiv3 scoClassifMatNiv3 = this.persistirScoClassifMatNiv3(cn2cn1GmtCodigo, cn2cn1Codigo, cn2Codigo, descricao);
		this.inserirScoClassifMatNiv4(cn2cn1GmtCodigo, cn2cn1Codigo, cn2Codigo, scoClassifMatNiv3.getId().getCodigo(), null);
	}

	/**
	 * RN04 de #5758
	 * 
	 * ORADB: SCOT_CN4_ASI
	 * 
	 * @param cn3cn2cn1GmtCodigo
	 * @param cn3cn2cn1Codigo
	 * @param cn3cn2Codigo
	 * @param cn3Codigo
	 * @param codigo
	 * @param descricao
	 * @param servidorLogado
	 */
	public void inserirScoClassifMatNiv4(Integer cn3cn2cn1GmtCodigo, Integer cn3cn2cn1Codigo, Integer cn3cn2Codigo, Integer cn3Codigo, String descricao) {
		// - Executar I4 e executar RN05 passando CODIGO=0 e DESCRICAO=NULL nos inserts subsequentes.
		ScoClassifMatNiv4 scoClassifMatNiv4 = this.persistirScoClassifMatNiv4(cn3cn2cn1GmtCodigo, cn3cn2cn1Codigo, cn3cn2Codigo, cn3Codigo, descricao);
		this.inserirScoClassifMatNiv5(scoClassifMatNiv4, null);
	}

	/**
	 * RN05 de #5758
	 * 
	 * ORADB: SCOT_CN5_ASI
	 * 
	 * @param scoClassifMatNiv4
	 * @param descricao
	 * @param servidorLogado
	 */
	public void inserirScoClassifMatNiv5(ScoClassifMatNiv4 scoClassifMatNiv4, String descricao) {
		// Executar I5.
		this.persistirScoClassifMatNiv5(scoClassifMatNiv4, descricao);
	}

	private void inserirScoClassifMatNiv1Jn(ScoClassifMatNiv1 scoClassifMatNiv1, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv1Jn scoClassifMatNiv1Jn = BaseJournalFactory.getBaseJournal(operacao, ScoClassifMatNiv1Jn.class, servidorLogado.getUsuario());
		scoClassifMatNiv1Jn.setCodigo(scoClassifMatNiv1.getId().getCodigo());
		scoClassifMatNiv1Jn.setCriadoEm(scoClassifMatNiv1.getCriadoEm());
		scoClassifMatNiv1Jn.setDescricao(scoClassifMatNiv1.getDescricao());
		scoClassifMatNiv1Jn.setGmtCodigo(scoClassifMatNiv1.getId().getGmtCodigo());
		scoClassifMatNiv1Jn.setSerMatricula(scoClassifMatNiv1.getSerMatricula());
		scoClassifMatNiv1Jn.setSerMatriculaAlterado(scoClassifMatNiv1.getSerMatriculaAlterado());
		scoClassifMatNiv1Jn.setSerVinCodigo(scoClassifMatNiv1.getSerVinCodigo());
		scoClassifMatNiv1Jn.setSerVinCodigoAlterado(scoClassifMatNiv1.getSerVinCodigoAlterado());
		getScoClassifMatNiv1JnDAO().persistir(scoClassifMatNiv1Jn);
	}

	private void inserirScoClassifMatNiv2Jn(ScoClassifMatNiv2 scoClassifMatNiv2, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv2Jn scoClassifMatNiv2Jn = BaseJournalFactory.getBaseJournal(operacao, ScoClassifMatNiv2Jn.class, servidorLogado.getUsuario());
		scoClassifMatNiv2Jn.setCodigo(scoClassifMatNiv2.getId().getCodigo());
		scoClassifMatNiv2Jn.setCriadoEm(scoClassifMatNiv2.getCriadoEm());
		scoClassifMatNiv2Jn.setDescricao(scoClassifMatNiv2.getDescricao());
		scoClassifMatNiv2Jn.setCn1GmtCodigo(scoClassifMatNiv2.getId().getCn1GmtCodigo());
		scoClassifMatNiv2Jn.setCn1Codigo(scoClassifMatNiv2.getId().getCn1Codigo());
		scoClassifMatNiv2Jn.setSerMatricula(scoClassifMatNiv2.getSerMatricula());
		scoClassifMatNiv2Jn.setSerMatriculaAlterado(scoClassifMatNiv2.getSerMatriculaAlterado());
		scoClassifMatNiv2Jn.setSerVinCodigo(scoClassifMatNiv2.getSerVinCodigo());
		scoClassifMatNiv2Jn.setSerVinCodigoAlterado(scoClassifMatNiv2.getSerVinCodigoAlterado());
		getScoClassifMatNiv2JnDAO().persistir(scoClassifMatNiv2Jn);
	}

	private void inserirScoClassifMatNiv3Jn(ScoClassifMatNiv3 scoClassifMatNiv3, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv3Jn scoClassifMatNiv3Jn = BaseJournalFactory.getBaseJournal(operacao, ScoClassifMatNiv3Jn.class, servidorLogado.getUsuario());
		scoClassifMatNiv3Jn.setCodigo(scoClassifMatNiv3.getId().getCodigo());
		scoClassifMatNiv3Jn.setCriadoEm(scoClassifMatNiv3.getCriadoEm());
		scoClassifMatNiv3Jn.setDescricao(scoClassifMatNiv3.getDescricao());
		scoClassifMatNiv3Jn.setCn2Cn1GmtCodigo(scoClassifMatNiv3.getId().getCn2Cn1GmtCodigo());
		scoClassifMatNiv3Jn.setCn2Cn1Codigo(scoClassifMatNiv3.getId().getCn2Cn1Codigo());
		scoClassifMatNiv3Jn.setCn2Codigo(scoClassifMatNiv3.getId().getCn2Codigo());
		scoClassifMatNiv3Jn.setSerMatricula(scoClassifMatNiv3.getSerMatricula());
		scoClassifMatNiv3Jn.setSerMatriculaAlterado(scoClassifMatNiv3.getSerMatriculaAlterado());
		scoClassifMatNiv3Jn.setSerVinCodigo(scoClassifMatNiv3.getSerVinCodigo());
		scoClassifMatNiv3Jn.setSerVinCodigoAlterado(scoClassifMatNiv3.getSerVinCodigoAlterado());
		getScoClassifMatNiv3JnDAO().persistir(scoClassifMatNiv3Jn);
	}

	private void inserirScoClassifMatNiv4Jn(ScoClassifMatNiv4 scoClassifMatNiv4, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv4Jn scoClassifMatNiv4Jn = BaseJournalFactory.getBaseJournal(operacao, ScoClassifMatNiv4Jn.class, servidorLogado.getUsuario());
		scoClassifMatNiv4Jn.setCodigo(scoClassifMatNiv4.getId().getCodigo());
		scoClassifMatNiv4Jn.setCriadoEm(scoClassifMatNiv4.getCriadoEm());
		scoClassifMatNiv4Jn.setDescricao(scoClassifMatNiv4.getDescricao());
		scoClassifMatNiv4Jn.setCn3Cn2Cn1GmtCodigo(scoClassifMatNiv4.getId().getCn3Cn2Cn1GmtCodigo());
		scoClassifMatNiv4Jn.setCn3Cn2Cn1Codigo(scoClassifMatNiv4.getId().getCn3Cn2Cn1Codigo());
		scoClassifMatNiv4Jn.setCn3Cn2Codigo(scoClassifMatNiv4.getId().getCn3Cn2Codigo());
		scoClassifMatNiv4Jn.setCn3Codigo(scoClassifMatNiv4.getId().getCn3Codigo());
		scoClassifMatNiv4Jn.setSerMatricula(scoClassifMatNiv4.getSerMatricula());
		scoClassifMatNiv4Jn.setSerMatriculaAlterado(scoClassifMatNiv4.getSerMatriculaAlterado());
		scoClassifMatNiv4Jn.setSerVinCodigo(scoClassifMatNiv4.getSerVinCodigo());
		scoClassifMatNiv4Jn.setSerVinCodigoAlterado(scoClassifMatNiv4.getSerVinCodigoAlterado());
		getScoClassifMatNiv4JnDAO().persistir(scoClassifMatNiv4Jn);
	}

	private void inserirScoClassifMatNiv5Jn(ScoClassifMatNiv5 scoClassifMatNiv5, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoClassifMatNiv5Jn scoClassifMatNiv5Jn = BaseJournalFactory.getBaseJournal(operacao, ScoClassifMatNiv5Jn.class, servidorLogado.getUsuario());
		scoClassifMatNiv5Jn.setCodigo(scoClassifMatNiv5.getCodigo());
		scoClassifMatNiv5Jn.setCriadoEm(scoClassifMatNiv5.getCriadoEm());
		scoClassifMatNiv5Jn.setDescricao(scoClassifMatNiv5.getDescricao());
		scoClassifMatNiv5Jn.setCn4Cn3Cn2Cn1GmtCodigo(scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Cn2Cn1GmtCodigo());
		scoClassifMatNiv5Jn.setCn4Cn3Cn2Cn1Codigo(scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Cn2Cn1Codigo());
		scoClassifMatNiv5Jn.setCn4Cn3Cn2Codigo(scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Cn2Codigo());
		scoClassifMatNiv5Jn.setCn4Cn3Codigo(scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCn3Codigo());
		scoClassifMatNiv5Jn.setCn4Codigo(scoClassifMatNiv5.getScoClassifMatNiv4().getId().getCodigo());
		scoClassifMatNiv5Jn.setSerMatricula(scoClassifMatNiv5.getSerMatricula());
		scoClassifMatNiv5Jn.setSerMatriculaAlterado(scoClassifMatNiv5.getSerMatriculaAlterado());
		scoClassifMatNiv5Jn.setSerVinCodigo(scoClassifMatNiv5.getSerVinCodigo());
		scoClassifMatNiv5Jn.setSerVinCodigoAlterado(scoClassifMatNiv5.getSerVinCodigoAlterado());
		scoClassifMatNiv5Jn.setNumero(scoClassifMatNiv5.getNumero());
		getScoClassifMatNiv5JnDAO().persistir(scoClassifMatNiv5Jn);
	}

	/**
	 * RN06 de #5758
	 * 
	 * ORADB: SCOT_CN1_ARU
	 * 
	 * @param scoClassifMatNiv1
	 * @param descricao
	 * @param servidorLogado
	 */
	public void atualizarScoClassifMatNiv1(ScoClassifMatNiv1 scoClassifMatNiv1, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		scoClassifMatNiv1 = getScoClassifMatNiv1DAO().obterPorChavePrimaria(scoClassifMatNiv1.getId());

		if (CoreUtil.modificados(scoClassifMatNiv1.getDescricao(), descricao)
				|| CoreUtil.modificados(scoClassifMatNiv1.getSerMatriculaAlterado(), servidorLogado.getId().getMatricula())
				|| CoreUtil.modificados(scoClassifMatNiv1.getSerVinCodigoAlterado(),servidorLogado.getId().getVinCodigo())) {
			// - Se houve alterações inserir registro na SCO_CLASSIF_MAT_NIV1_JN.
			this.inserirScoClassifMatNiv1Jn(scoClassifMatNiv1, DominioOperacoesJournal.UPD);
		}
		// - Executar U1.
		scoClassifMatNiv1.setDescricao(descricao);
		scoClassifMatNiv1.setSerMatriculaAlterado(servidorLogado.getId().getMatricula());
		scoClassifMatNiv1.setSerVinCodigoAlterado(servidorLogado.getId().getVinCodigo());
		getScoClassifMatNiv1DAO().atualizar(scoClassifMatNiv1);
	}

	/**
	 * RN07 de #5758
	 * 
	 * ORADB: SCOT_CN2_ARU
	 * 
	 * @param scoClassifMatNiv2
	 * @param descricao
	 * @param servidorLogado
	 */
	public void atualizarScoClassifMatNiv2(ScoClassifMatNiv2 scoClassifMatNiv2, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		scoClassifMatNiv2 = getScoClassifMatNiv2DAO().obterPorChavePrimaria(scoClassifMatNiv2.getId());

		if (CoreUtil.modificados(scoClassifMatNiv2.getDescricao(), descricao)
				|| CoreUtil.modificados(scoClassifMatNiv2.getSerMatriculaAlterado(), servidorLogado.getId().getMatricula())
				|| CoreUtil.modificados(scoClassifMatNiv2.getSerVinCodigoAlterado(),servidorLogado.getId().getVinCodigo())) {
			// - Se houve alterações inserir registro na SCO_CLASSIF_MAT_NIV2_JN.
			this.inserirScoClassifMatNiv2Jn(scoClassifMatNiv2, DominioOperacoesJournal.UPD);
		}
		// - Executar U2.
		scoClassifMatNiv2.setDescricao(descricao);
		scoClassifMatNiv2.setSerMatriculaAlterado(servidorLogado.getId().getMatricula());
		scoClassifMatNiv2.setSerVinCodigoAlterado(servidorLogado.getId().getVinCodigo());
		getScoClassifMatNiv2DAO().atualizar(scoClassifMatNiv2);
	}

	/**
	 * RN08 de #5758
	 * 
	 * ORADB: SCOT_CN3_ARU
	 * 
	 * @param scoClassifMatNiv3
	 * @param descricao
	 * @param servidorLogado
	 */
	public void atualizarScoClassifMatNiv3(ScoClassifMatNiv3 scoClassifMatNiv3, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		scoClassifMatNiv3 = getScoClassifMatNiv3DAO().obterPorChavePrimaria(scoClassifMatNiv3.getId());

		if (CoreUtil.modificados(scoClassifMatNiv3.getDescricao(), descricao)
				|| CoreUtil.modificados(scoClassifMatNiv3.getSerMatriculaAlterado(), servidorLogado.getId().getMatricula())
				|| CoreUtil.modificados(scoClassifMatNiv3.getSerVinCodigoAlterado(),servidorLogado.getId().getVinCodigo())) {
			// - Se houve alterações inserir registro na SCO_CLASSIF_MAT_NIV3_JN.
			this.inserirScoClassifMatNiv3Jn(scoClassifMatNiv3, DominioOperacoesJournal.UPD);
		}
		// - Executar U3.
		scoClassifMatNiv3.setDescricao(descricao);
		scoClassifMatNiv3.setSerMatriculaAlterado(servidorLogado.getId().getMatricula());
		scoClassifMatNiv3.setSerVinCodigoAlterado(servidorLogado.getId().getVinCodigo());
		getScoClassifMatNiv3DAO().atualizar(scoClassifMatNiv3);
	}

	/**
	 * RN09 de #5758
	 * 
	 * ORADB: SCOT_CN4_ARU
	 * 
	 * @param scoClassifMatNiv4
	 * @param descricao
	 * @param servidorLogado
	 */
	public void atualizarScoClassifMatNiv4(ScoClassifMatNiv4 scoClassifMatNiv4, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		scoClassifMatNiv4 = getScoClassifMatNiv4DAO().obterPorChavePrimaria(scoClassifMatNiv4.getId());

		if (CoreUtil.modificados(scoClassifMatNiv4.getDescricao(), descricao)
				|| CoreUtil.modificados(scoClassifMatNiv4.getSerMatriculaAlterado(), servidorLogado.getId().getMatricula())
				|| CoreUtil.modificados(scoClassifMatNiv4.getSerVinCodigoAlterado(),servidorLogado.getId().getVinCodigo())) {
			// - Se houve alterações inserir registro na SCO_CLASSIF_MAT_NIV4_JN.
			this.inserirScoClassifMatNiv4Jn(scoClassifMatNiv4, DominioOperacoesJournal.UPD);
		}
		// - Executar U4.
		scoClassifMatNiv4.setDescricao(descricao);
		scoClassifMatNiv4.setSerMatriculaAlterado(servidorLogado.getId().getMatricula());
		scoClassifMatNiv4.setSerVinCodigoAlterado(servidorLogado.getId().getVinCodigo());
		getScoClassifMatNiv4DAO().atualizar(scoClassifMatNiv4);
	}

	/**
	 * RN10 de #5758
	 * 
	 * ORADB: SCOT_CN5_ARU
	 * 
	 * @param scoClassifMatNiv5
	 * @param descricao
	 * @param servidorLogado
	 */
	public void atualizarScoClassifMatNiv5(ScoClassifMatNiv5 scoClassifMatNiv5, String descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		scoClassifMatNiv5 = getScoClassifMatNiv5DAO().obterPorChavePrimaria(scoClassifMatNiv5.getNumero());

		if (CoreUtil.modificados(scoClassifMatNiv5.getDescricao(), descricao)
				|| CoreUtil.modificados(scoClassifMatNiv5.getSerMatriculaAlterado(), servidorLogado.getId().getMatricula())
				|| CoreUtil.modificados(scoClassifMatNiv5.getSerVinCodigoAlterado(),servidorLogado.getId().getVinCodigo())) {
			// - Se houve alterações inserir registro na SCO_CLASSIF_MAT_NIV5_JN.
			this.inserirScoClassifMatNiv5Jn(scoClassifMatNiv5, DominioOperacoesJournal.UPD);
		}
		// - Executar U5.
		scoClassifMatNiv5.setDescricao(descricao);
		scoClassifMatNiv5.setSerMatriculaAlterado(servidorLogado.getId().getMatricula());
		scoClassifMatNiv5.setSerVinCodigoAlterado(servidorLogado.getId().getVinCodigo());
		getScoClassifMatNiv5DAO().atualizar(scoClassifMatNiv5);
	}

	/**
	 * RN11 de #5758
	 * 
	 * ORADB: SCOT_CN1_ARD
	 * 
	 * @param scoClassifMatNiv1
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void removerScoClassifMatNiv1(ScoClassifMatNiv1 scoClassifMatNiv1) throws ApplicationBusinessException {

		scoClassifMatNiv1 = getScoClassifMatNiv1DAO().obterPorChavePrimaria(scoClassifMatNiv1.getId());
		
		if(scoClassifMatNiv1.getScoClassifMatNiv2s() != null && !scoClassifMatNiv1.getScoClassifMatNiv2s().isEmpty()){
			throw new ApplicationBusinessException(ScoClassifMatRNExceptionCode.M6_CLASSIFICACAO_MATERIAL);
		}

		// inserir registro na SCO_CLASSIF_MAT_NIV1_JN.
		this.inserirScoClassifMatNiv1Jn(scoClassifMatNiv1, DominioOperacoesJournal.DEL);

		// - Executar D1.
		getScoClassifMatNiv1DAO().remover(scoClassifMatNiv1);
	}

	/**
	 * RN12 de #5758
	 * 
	 * ORADB: SCOT_CN2_ARD
	 * 
	 * @param scoClassifMatNiv2
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void removerScoClassifMatNiv2(ScoClassifMatNiv2 scoClassifMatNiv2) throws ApplicationBusinessException {

		scoClassifMatNiv2 = getScoClassifMatNiv2DAO().obterPorChavePrimaria(scoClassifMatNiv2.getId());
		
		if(scoClassifMatNiv2.getScoClassifMatNiv3s() != null && !scoClassifMatNiv2.getScoClassifMatNiv3s().isEmpty()){
			throw new ApplicationBusinessException(ScoClassifMatRNExceptionCode.M7_CLASSIFICACAO_MATERIAL);
		}

		// inserir registro na SCO_CLASSIF_MAT_NIV2_JN.
		this.inserirScoClassifMatNiv2Jn(scoClassifMatNiv2, DominioOperacoesJournal.DEL);

		// - Executar D2.
		getScoClassifMatNiv2DAO().remover(scoClassifMatNiv2);
	}

	/**
	 * RN13 de #5758
	 * 
	 * ORADB: SCOT_CN3_ARD
	 * 
	 * @param scoClassifMatNiv3
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void removerScoClassifMatNiv3(ScoClassifMatNiv3 scoClassifMatNiv3) throws ApplicationBusinessException {

		scoClassifMatNiv3 = getScoClassifMatNiv3DAO().obterPorChavePrimaria(scoClassifMatNiv3.getId());
		
		if(scoClassifMatNiv3.getScoClassifMatNiv4s() != null && !scoClassifMatNiv3.getScoClassifMatNiv4s().isEmpty()){
			throw new ApplicationBusinessException(ScoClassifMatRNExceptionCode.M8_CLASSIFICACAO_MATERIAL);
		}

		// inserir registro na SCO_CLASSIF_MAT_NIV3_JN.
		this.inserirScoClassifMatNiv3Jn(scoClassifMatNiv3, DominioOperacoesJournal.DEL);

		// - Executar D3.
		getScoClassifMatNiv3DAO().remover(scoClassifMatNiv3);
	}

	/**
	 * RN14 de #5758
	 * 
	 * ORADB: SCOT_CN4_ARD
	 * 
	 * @param scoClassifMatNiv4
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void removerScoClassifMatNiv4(ScoClassifMatNiv4 scoClassifMatNiv4) throws ApplicationBusinessException {

		scoClassifMatNiv4 = getScoClassifMatNiv4DAO().obterPorChavePrimaria(scoClassifMatNiv4.getId());

		if(scoClassifMatNiv4.getScoClassifMatNiv5s() != null && !scoClassifMatNiv4.getScoClassifMatNiv5s().isEmpty()){
			throw new ApplicationBusinessException(ScoClassifMatRNExceptionCode.M9_CLASSIFICACAO_MATERIAL);
		}
		
		// inserir registro na SCO_CLASSIF_MAT_NIV4_JN.
		this.inserirScoClassifMatNiv4Jn(scoClassifMatNiv4, DominioOperacoesJournal.DEL);

		// - Executar D4.
		getScoClassifMatNiv4DAO().remover(scoClassifMatNiv4);
	}

	/**
	 * RN15 de #5758
	 * 
	 * ORADB: SCOT_CN5_ARD
	 * 
	 * @param scoClassifMatNiv5
	 * @param servidorLogado
	 */
	public void removerScoClassifMatNiv5(ScoClassifMatNiv5 scoClassifMatNiv5) {

		scoClassifMatNiv5 = getScoClassifMatNiv5DAO().obterPorChavePrimaria(scoClassifMatNiv5.getNumero());

		// inserir registro na SCO_CLASSIF_MAT_NIV5_JN.
		this.inserirScoClassifMatNiv5Jn(scoClassifMatNiv5, DominioOperacoesJournal.DEL);

		// - Executar D5.
		getScoClassifMatNiv5DAO().remover(scoClassifMatNiv5);
	}

	// ------------------------------ DAO

	protected ScoClassifMatNiv1DAO getScoClassifMatNiv1DAO() {
		return scoClassifMatNiv1DAO;
	}

	protected ScoClassifMatNiv2DAO getScoClassifMatNiv2DAO() {
		return scoClassifMatNiv2DAO;
	}

	protected ScoClassifMatNiv3DAO getScoClassifMatNiv3DAO() {
		return scoClassifMatNiv3DAO;
	}

	protected ScoClassifMatNiv4DAO getScoClassifMatNiv4DAO() {
		return scoClassifMatNiv4DAO;
	}

	protected ScoClassifMatNiv5DAO getScoClassifMatNiv5DAO() {
		return scoClassifMatNiv5DAO;
	}

	protected ScoClassifMatNiv1JnDAO getScoClassifMatNiv1JnDAO() {
		return scoClassifMatNiv1JnDAO;
	}

	protected ScoClassifMatNiv2JnDAO getScoClassifMatNiv2JnDAO() {
		return scoClassifMatNiv2JnDAO;
	}

	protected ScoClassifMatNiv3JnDAO getScoClassifMatNiv3JnDAO() {
		return scoClassifMatNiv3JnDAO;
	}

	protected ScoClassifMatNiv4JnDAO getScoClassifMatNiv4JnDAO() {
		return scoClassifMatNiv4JnDAO;
	}

	protected ScoClassifMatNiv5JnDAO getScoClassifMatNiv5JnDAO() {
		return scoClassifMatNiv5JnDAO;
	}
}
