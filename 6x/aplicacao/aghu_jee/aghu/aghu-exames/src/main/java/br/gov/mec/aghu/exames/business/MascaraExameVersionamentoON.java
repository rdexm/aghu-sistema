package br.gov.mec.aghu.exames.business;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.dao.AelCampoCodifRelacionadoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoRelacionadoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoVinculadoDAO;
import br.gov.mec.aghu.exames.dao.AelDescricoesResulPadraoDAO;
import br.gov.mec.aghu.exames.dao.AelExameEquipCampoLaudDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoPadraoCampoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadosPadraoDAO;
import br.gov.mec.aghu.model.AelCampoCodifRelacionado;
import br.gov.mec.aghu.model.AelCampoLaudoRelacionado;
import br.gov.mec.aghu.model.AelCampoVinculado;
import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelExameEquipCampoLaud;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class MascaraExameVersionamentoON extends BaseBusiness {

	
	@EJB
	private AelExameEquipCampoLaudRN aelExameEquipCampoLaudRN;
	
	private static final Log LOG = LogFactory.getLog(MascaraExameVersionamentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelExameEquipCampoLaudDAO aelExameEquipCampoLaudDAO;
	
	@Inject
	private AelCampoLaudoRelacionadoDAO aelCampoLaudoRelacionadoDAO;
	
	@Inject
	private AelResultadoPadraoCampoDAO aelResultadoPadraoCampoDAO;
	
	@Inject
	private AelCampoVinculadoDAO aelCampoVinculadoDAO;
	
	@Inject
	private AelDescricoesResulPadraoDAO aelDescricoesResulPadraoDAO;
	
	@Inject
	private AelResultadosPadraoDAO aelResultadosPadraoDAO;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private AelCampoCodifRelacionadoDAO aelCampoCodifRelacionadoDAO;
	
	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1387007070169781L;
	
	
	
	/**
	 * PROCEDURE TFormVersaoLaudo.NovaVersao
	 * 
	 * Responsável por incluir novas versões <br> 
	 * de máscaras baseadas em máscaras existentes.
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	public AelVersaoLaudo criarNovaVersao(AelVersaoLaudo versaoLaudo) throws BaseException {
		
		AelVersaoLaudo novaVersao = new AelVersaoLaudo();
		try {
			PropertyUtils.copyProperties(novaVersao, versaoLaudo);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}		
			
		novaVersao.setId(null);
		novaVersao.setAelVersaoLaudoses(null);
		this.getCadastrosApoioExamesFacade().persistirVersaoLaudo(novaVersao);
		
		//RN1: Recupera registros de Parâmetro Campos Laudo da Máscara e os insere com o novo seqp incrementado.
		List<AelParametroCamposLaudo> novoParametrosCampoLaudoList = this.inserirNovoParametroCampoLaudo(versaoLaudo, novaVersao);
		
		final Map<AelParametroCampoLaudoId, AelParametroCamposLaudo> mapParametrosCampoLaudo = new HashMap<AelParametroCampoLaudoId, AelParametroCamposLaudo>();
		CollectionUtils.forAllDo(novoParametrosCampoLaudoList, new Closure() {
			@Override
			public void execute(Object obj) {
				AelParametroCamposLaudo parametro = (AelParametroCamposLaudo) obj; 
				mapParametrosCampoLaudo.put(parametro.getId(), parametro);					
			}
		});

		//RN2: Recupera registros de Campos Vinculados da Máscara e os insere com o novo seqp do parâmetro inserido na RN1.
		this.inserirNovoCampoVinculado(versaoLaudo, novaVersao, mapParametrosCampoLaudo);
		
		//RN3: Recupera registros de Campos Relacionados Codificados da Máscara e os insere com o novo seqp do parâmetro inserido na RN1.
		this.inserirNovoCampoCodifRelacionado(versaoLaudo, novaVersao, mapParametrosCampoLaudo);
		
		//RN4: Recupera registros de Campos Relacionados da Máscara e os insere com o novo seqp do parâmetro inserido na RN1. 
		this.inserirNovoCampoLaudoRelacionado(versaoLaudo, novaVersao, mapParametrosCampoLaudo);
		
		//RN5: Recupera registros de Resultados Padrões Campos da Máscara e os insere com o novo seqp do parâmetro inserido na RN1. 
		this.inserirNovoResultadoPadraoCampo(versaoLaudo, novaVersao, mapParametrosCampoLaudo);
		
		//RN7: Recupera registros de Equipamentos e os insere com o novo seqp do parâmetro inserido na RN1.
		this.inserirNovoExameEquipCampoLaud(versaoLaudo, novaVersao, mapParametrosCampoLaudo);
		
		return novaVersao;
	}
	
	
	/**
	 * Recupera registros de Parâmetro Campos<br>
	 * Laudo da Máscara e os insere com o novo seqp incrementado.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @throws BaseException
	 */
	private List<AelParametroCamposLaudo> inserirNovoParametroCampoLaudo(AelVersaoLaudo versaoLaudoOld, AelVersaoLaudo versaoLaudoNew) throws BaseException {
		List<AelParametroCamposLaudo> camposLaudoList = this.getAelParametroCamposLaudoDAO()
			.pesquisarParametroCamposLaudoPorVersaoLaudo(versaoLaudoOld.getId().getEmaExaSigla(), versaoLaudoOld.getId().getEmaManSeq(), versaoLaudoOld.getId().getSeqp());
		List<AelParametroCamposLaudo> novoParametrosCampoLaudoList = new LinkedList<AelParametroCamposLaudo>();
		
		for (AelParametroCamposLaudo aelParametroCamposLaudo : camposLaudoList) {
			AelParametroCamposLaudo novoElemento = new AelParametroCamposLaudo();
			
			try {
				BeanUtils.copyProperties(novoElemento, aelParametroCamposLaudo);
			} catch (IllegalAccessException e) {
				LOG.error(e.getMessage());
			} catch (InvocationTargetException e) {
				LOG.error(e.getMessage());
			}
			
			novoElemento.setResultadosExames(null);
			novoElemento.setCampoVinculados(null);
			novoElemento.setCamposLaudoRelacionados(null);
			novoElemento.setCampoCodifRelacionado(null);
			novoElemento.setAelVersaoLaudo(versaoLaudoNew);
			novoParametrosCampoLaudoList.add(novoElemento);
		}
		
		this.getCadastrosApoioExamesFacade().persistirParametroCamposLaudo(novoParametrosCampoLaudoList, true);

		return novoParametrosCampoLaudoList;
	}
	
			
	
	/**
	 * Recupera registros de Campos Vinculados<br> 
	 * da Máscara e os insere com o novo seqp do<br> 
	 * parâmetro inserido na RN1 do método inserirNovoParametroCampoLaudo.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @throws BaseException
	 */
	protected void inserirNovoCampoVinculado(AelVersaoLaudo versaoLaudoOld, AelVersaoLaudo versaoLaudoNew, Map<AelParametroCampoLaudoId, AelParametroCamposLaudo> mapParametrosCampoLaudo) throws IllegalArgumentException, BaseException {
		List<AelCampoVinculado> camposVinculadosList = this.getAelCampoVinculadoDAO().pesquisarCampoVinculadoPorParametroCampoLaudo(
				versaoLaudoOld.getId().getEmaExaSigla(), 
				versaoLaudoOld.getId().getEmaManSeq(), 
				versaoLaudoOld.getId().getSeqp()
		);
		
		for (AelCampoVinculado aelCampoVinculado : camposVinculadosList) {
			final AelParametroCamposLaudo pesquisa1 = aelCampoVinculado.getAelParametroCamposLaudoByAelCvcPclFk1();
			pesquisa1.getId().setVelSeqp(versaoLaudoNew.getId().getSeqp());
			final AelParametroCamposLaudo pesquisa2 = aelCampoVinculado.getAelParametroCamposLaudoByAelCvcPclFk2();
			pesquisa2.getId().setVelSeqp(versaoLaudoNew.getId().getSeqp());
			
			AelParametroCamposLaudo aelCvcPclFk1 = null;
			AelParametroCamposLaudo aelCvcPclFk2 = null;
			aelCvcPclFk1 = mapParametrosCampoLaudo.get(pesquisa1.getId()); 
			aelCvcPclFk2 = mapParametrosCampoLaudo.get(pesquisa2.getId());
			
			AelCampoVinculado newCampoVinculado = new AelCampoVinculado();
			newCampoVinculado.setAelParametroCamposLaudoByAelCvcPclFk1(aelCvcPclFk1);
			newCampoVinculado.setAelParametroCamposLaudoByAelCvcPclFk2(aelCvcPclFk2);
			
			this.getCadastrosApoioExamesFacade().inserirAelCampoVinculado(newCampoVinculado);
		}
	}
	
	
	
	/**
	 * Recupera registros de Campos Relacionados<br> 
	 * Codificados da Máscara e os insere com o novo<br> 
	 * seqp do parâmetro inserido na RN1 do método inserirNovoParametroCampoLaudo.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @throws BaseException
	 */
	private void inserirNovoCampoCodifRelacionado(AelVersaoLaudo versaoLaudoOld, AelVersaoLaudo versaoLaudoNew, Map<AelParametroCampoLaudoId, AelParametroCamposLaudo> mapParametrosCampoLaudo) throws BaseException {
		List<AelCampoCodifRelacionado> campoCodifRelacionadoList = this.getAelCampoCodifRelacionadoDAO()
			.listarCamposCodifRelacionadosPorExameMaterial(versaoLaudoOld.getId().getEmaExaSigla(), versaoLaudoOld.getId().getEmaManSeq(), versaoLaudoOld.getId().getSeqp());

		for (AelCampoCodifRelacionado campoCodifRelacionado : campoCodifRelacionadoList) {
			final AelParametroCamposLaudo pesquisa1 = campoCodifRelacionado.getAelParametroCamposLaudoByAelCcrPclFk1();
			pesquisa1.getId().setVelSeqp(versaoLaudoNew.getId().getSeqp());
			final AelParametroCamposLaudo pesquisa2 = campoCodifRelacionado.getAelParametroCamposLaudoByAelCcrPclFk2();
			pesquisa2.getId().setVelSeqp(versaoLaudoNew.getId().getSeqp());

			AelParametroCamposLaudo aelCvcPclFk1 = mapParametrosCampoLaudo.get(pesquisa1.getId());
			AelParametroCamposLaudo aelCvcPclFk2 = mapParametrosCampoLaudo.get(pesquisa2.getId());
			
			AelCampoCodifRelacionado novoElemento = new AelCampoCodifRelacionado();
			novoElemento.setAelParametroCamposLaudoByAelCcrPclFk1(aelCvcPclFk1);
			novoElemento.setAelParametroCamposLaudoByAelCcrPclFk2(aelCvcPclFk2);
			
			this.getCadastrosApoioExamesFacade().inserirAelCampoCodifRelacionado(novoElemento);
		}
	}
	
	
	/**
	 * Recupera registros de Campos Relacionados<br> 
	 * da Máscara e os insere com o novo seqp do parâmetro<br> 
	 * inserido na RN1 do método inserirNovoParametroCampoLaudo.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @throws BaseException
	 */
	private void inserirNovoCampoLaudoRelacionado(AelVersaoLaudo versaoLaudoOld, AelVersaoLaudo versaoLaudoNew, Map<AelParametroCampoLaudoId, AelParametroCamposLaudo> mapParametrosCampoLaudo) throws BaseException {
		List<AelCampoLaudoRelacionado> campoLaudoRelacionadoList = this.getAelCampoLaudoRelacionadoDAO()
			.listarCampoLaudoRelacionadoPorExameMaterial(versaoLaudoOld.getId().getEmaExaSigla(), 
					versaoLaudoOld.getId().getEmaManSeq(), versaoLaudoOld.getId().getSeqp());
		
		for (AelCampoLaudoRelacionado campoLaudoRelacionado : campoLaudoRelacionadoList) {
			final AelParametroCamposLaudo pesquisa1 = campoLaudoRelacionado.getAelParametroCamposLaudoByAelClvPclFk1();
			pesquisa1.getId().setVelSeqp(versaoLaudoNew.getId().getSeqp());
			final AelParametroCamposLaudo pesquisa2 = campoLaudoRelacionado.getAelParametroCamposLaudoByAelClvPclFk2();
			pesquisa2.getId().setVelSeqp(versaoLaudoNew.getId().getSeqp());

			AelParametroCamposLaudo aelCvcPclFk1 = mapParametrosCampoLaudo.get(pesquisa1.getId());
			AelParametroCamposLaudo aelCvcPclFk2 = mapParametrosCampoLaudo.get(pesquisa2.getId());
			
			AelCampoLaudoRelacionado novoElemento = new AelCampoLaudoRelacionado();
			novoElemento.setAelParametroCamposLaudoByAelClvPclFk1(aelCvcPclFk1);
			novoElemento.setAelParametroCamposLaudoByAelClvPclFk2(aelCvcPclFk2);
			
			this.getCadastrosApoioExamesFacade().inserirAelCampoLaudoRelacionado(novoElemento);
		}
	}
	

	/**
	 * Recupera registros de Resultados Padrões<br> 
	 * Campos da Máscara e os insere com o novo seqp<br> 
	 * do parâmetro inserido na RN1 do método inserirNovoParametroCampoLaudo.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @throws BaseException
	 */
	private void inserirNovoResultadoPadraoCampo(AelVersaoLaudo versaoLaudoOld, AelVersaoLaudo versaoLaudoNew, Map<AelParametroCampoLaudoId, AelParametroCamposLaudo> mapParametrosCampoLaudo) throws BaseException {
		List<AelResultadoPadraoCampo> resultadoPadraoCampoList = this.getAelResultadoPadraoCampoDAO()
			.listarResultadoPadraoCampoPorExameMaterial(versaoLaudoOld.getId().getEmaExaSigla(), 
					versaoLaudoOld.getId().getEmaManSeq(), versaoLaudoOld.getId().getSeqp());
		
		final AelResultadosPadrao resultadoPadrao = this.getAelResultadosPadraoDAO().obterResultadosPadraoPorExame(
				versaoLaudoOld.getId().getEmaExaSigla(), versaoLaudoOld.getId().getEmaManSeq());
		
		for (AelResultadoPadraoCampo resultadoPadraoCampo : resultadoPadraoCampoList) {
			// RN5 AelResultadoPadraoCampo
			final AelParametroCamposLaudo pesquisa1 = resultadoPadraoCampo.getParametroCampoLaudo();
			pesquisa1.getId().setVelSeqp(versaoLaudoNew.getId().getSeqp());
			
			AelParametroCamposLaudo aelCvcPclFk1 = mapParametrosCampoLaudo.get(pesquisa1.getId());
			
			AelResultadoPadraoCampo novoElemento = new AelResultadoPadraoCampo();
			novoElemento.setParametroCampoLaudo(aelCvcPclFk1);
			novoElemento.setResultadoPadrao(resultadoPadrao);
			novoElemento.setCampoLaudo(resultadoPadraoCampo.getCampoLaudo());
			novoElemento.setResultadoCaracteristica(resultadoPadraoCampo.getResultadoCaracteristica());
			novoElemento.setResultadoCodificado(resultadoPadraoCampo.getResultadoCodificado());
			novoElemento.setValor(resultadoPadraoCampo.getValor());
			
			this.getCadastrosApoioExamesFacade().persistirAelResultadoPadraoCampo(novoElemento);
			
			/* RN6: Recupera registros de Descrições de Resultados Padrões Campos da Máscara 
			 * e os insere com o novo seqp do parâmetro inserido na RN1.
			 */

			AelDescricoesResulPadrao descResultaPadrao = this.getAelDescricoesResulPadraoDAO().obterAelDescricoesResulPadraoPorId(resultadoPadraoCampo.getId().getRpaSeq(), resultadoPadraoCampo.getId().getSeqp());
			if(descResultaPadrao != null) { 
				AelDescricoesResulPadrao novoDescricaoResultaPadrao = new AelDescricoesResulPadrao();
				novoDescricaoResultaPadrao.setResultadoPadraoCampo(novoElemento);
				// Seta nova descrição
				novoDescricaoResultaPadrao.setDescricao(descResultaPadrao.getDescricao());
				// Persiste nova descrição
				this.getCadastrosApoioExamesFacade().persistirAelDescricoesResulPadrao(novoDescricaoResultaPadrao);
			}
		}
				
		
	}
	
	
	/**
	 * Recupera registros de Equipamentos<br> 
	 * e os insere com o novo seqp do parâmetro<br> 
	 * inserido na RN1 do método inserirNovoParametroCampoLaudo.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @throws BaseException
	 */
	private void inserirNovoExameEquipCampoLaud(AelVersaoLaudo versaoLaudoOld, AelVersaoLaudo versaoLaudoNew, Map<AelParametroCampoLaudoId, AelParametroCamposLaudo> mapParametrosCampoLaudo) throws BaseException {
		List<AelExameEquipCampoLaud> exameEquipCampoLaudoList = this.getAelExameEquipCampoLaudDAO()
			.listarExameEquipamentoLaudoPorMaterialExame(versaoLaudoOld.getId().getEmaExaSigla()
					, versaoLaudoOld.getId().getEmaManSeq(), versaoLaudoOld.getId().getSeqp());
		
		for (AelExameEquipCampoLaud aelExameEquipCampoLaud : exameEquipCampoLaudoList) {
			AelExameEquipCampoLaud novoElemento = new AelExameEquipCampoLaud();
			
			final AelParametroCamposLaudo pesquisa1 = aelExameEquipCampoLaud.getAelParametroCamposLaudo();
			pesquisa1.getId().setVelSeqp(versaoLaudoNew.getId().getSeqp());
			AelParametroCamposLaudo aelCvcPclFk1 = mapParametrosCampoLaudo.get(pesquisa1.getId());
			
			novoElemento.setAelParametroCamposLaudo(aelCvcPclFk1);
			novoElemento.setAelExameEquipamento(aelExameEquipCampoLaud.getAelExameEquipamento());
			novoElemento.setOrdem(aelExameEquipCampoLaud.getOrdem());
			
			this.getAelExameEquipCampoLaudRN().inserir(novoElemento);
		}
	}
	
	protected ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}
	
	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}
	
	protected AelCampoVinculadoDAO getAelCampoVinculadoDAO() {
		return aelCampoVinculadoDAO;
	}
	
	protected AelCampoCodifRelacionadoDAO getAelCampoCodifRelacionadoDAO() {
		return aelCampoCodifRelacionadoDAO;
	}

	protected AelCampoLaudoRelacionadoDAO getAelCampoLaudoRelacionadoDAO() {
		return aelCampoLaudoRelacionadoDAO;
	}
	
	protected AelResultadoPadraoCampoDAO getAelResultadoPadraoCampoDAO() {
		return aelResultadoPadraoCampoDAO;
	}
	
	protected AelExameEquipCampoLaudDAO getAelExameEquipCampoLaudDAO() {
		return aelExameEquipCampoLaudDAO;
	}

	protected AelExameEquipCampoLaudRN getAelExameEquipCampoLaudRN() {
		return aelExameEquipCampoLaudRN;
	}
	
	protected AelDescricoesResulPadraoDAO getAelDescricoesResulPadraoDAO() {
		return aelDescricoesResulPadraoDAO;
	}
	
	protected AelResultadosPadraoDAO getAelResultadosPadraoDAO() {
		return aelResultadosPadraoDAO;
	}
}
