package br.gov.mec.aghu.perinatologia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.exames.vo.RegiaoAnatomicaVO;
import br.gov.mec.aghu.model.McoAchado;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoAchadoDAO;
import br.gov.mec.aghu.perinatologia.vo.AchadoVO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class McoAchadoRN  extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7070905230600277991L;

	@Inject
	private McoAchadoDAO mcoAchadoDAO;
	
	@Inject
	private IExamesService exameService;
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return null;
	}
	
	
	private enum McoAchadoRNException implements BusinessExceptionCode {
			MENSAGEM_ERRO_ACHADO_JA_CADASTRADO  
	}	

	//C1
	public List<AchadoVO> pesquisarAchadosPorDescSituacaoRegiaoAnatomicas(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, AchadoVO filtro) {
		List<AchadoVO> achadosVO = new ArrayList<AchadoVO>();
		//C4
		// Lista de VO, com dados do emergencia e do monolito, então força a ordenação pelo Java.
		String ordernarPor = orderProperty;
		orderProperty = validarOrdenacao(orderProperty);
		List<McoAchado> achados = this.mcoAchadoDAO.buscarAchadosPorDescSituacao(filtro.getDescricaoAcd(), filtro.getSituacao(), filtro.getSeqRan(), firstResult,  maxResults,orderProperty, asc);
		// Se não encontrou nenhum achado do lado do emergencia nem chama o monolito
		if(!isNotListaVazia(achados)) {
			return achadosVO;
		}
		//C1
		 achadosVO = parseListaToVO(filtro, achados);
		if(isNotListaVazia(achadosVO)) {		
			ordernarLista(asc, achadosVO, ordernarPor);
		}
		return achadosVO;
	}
	
	private List<AchadoVO> parseListaToVO(AchadoVO filtro, List<McoAchado> achados) {
		List<AchadoVO> resultado = new ArrayList<AchadoVO> ();
		for (McoAchado item : achados) {				
				RegiaoAnatomicaVO regiao = exameService.obterRegiaoAnotomicaPorId(item.getAelRegiaoAnatomica().getSeq());
				if(regiao == null) {
					resultado.add(criarAchadoVO(item, new RegiaoAnatomicaVO()));
				} else {
					resultado.add(criarAchadoVO(item, regiao));
			}
		}
		return resultado;
	}

	private void ordernarLista(boolean asc, List<AchadoVO> resultado,
			String ordernarPor) {
		if(StringUtils.isNotBlank(ordernarPor)){
			CoreUtil.ordenarLista(resultado, ordernarPor, asc);
		} else {
			java.util.Collections.sort(resultado);
		}
	}

	private String validarOrdenacao(String orderProperty) {
		if(StringUtils.isNotBlank(orderProperty)) {
			orderProperty = null;
		}
		return orderProperty;
	}
	
	//C2
	public List<RegiaoAnatomicaVO> pesquisarRegioesAnatomicasAtivasPorDesc(String descricao) {
		return this.exameService.pesquisarRegioesAnatomicasAtivasPorDescricaoSeq(descricao);
	}

	//C3
	private Boolean existeAchado(String descAchado, String descRegiao) {
		List<Integer> codigos = this.mcoAchadoDAO.buscarRanSeqPorDescricaoAchado(descAchado);
		if(isNotListaVazia(codigos)) {
			if(StringUtils.isNotBlank(descRegiao)) {
				return this.exameService.verificarRegioesPorSeqAchadoDescricao(codigos, descRegiao);
			}
		}
		return Boolean.FALSE;
	}
	
	private void isAchadoDuplicado(McoAchado entity, String descricaoRegiao) throws ApplicationBusinessException {
		if(existeAchado(entity.getDescricao(), descricaoRegiao)) {
			throw new ApplicationBusinessException(McoAchadoRNException.MENSAGEM_ERRO_ACHADO_JA_CADASTRADO);
		}
	}

	private AchadoVO criarAchadoVO(McoAchado achado, RegiaoAnatomicaVO regiao) {
		AchadoVO achadoVO = new AchadoVO();
		achadoVO.setDescricaoAcd(achado.getDescricao());
		achadoVO.setDescricaoRan(regiao.getDescricao());
		achadoVO.setIndExigeObs(achado.getIndExigeObs());
		achadoVO.setMensagemAlerta(achado.getMensagemAlerta());
		achadoVO.setSeq(achado.getSeq());
		achadoVO.setSituacao(achado.getIndSituacao());
		return achadoVO;
	}
	
	@SuppressWarnings("rawtypes")
	private Boolean isNotListaVazia(List lista) {
		return lista != null && !lista.isEmpty();
	}

	//RN01
	public void inserirAchado(McoAchado entity, String descricaoRegiao) throws ApplicationBusinessException {
		isAchadoDuplicado(entity, descricaoRegiao);
		entity.setCriadoEm(new Date());
		salvarDadosServidor(entity);
		mcoAchadoDAO.persistir(entity);
	}

	private void salvarDadosServidor(McoAchado entity) {
		
		entity.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
	}
	
	//RN02
	public void atualizarAchado(McoAchado entity) {
		salvarDadosServidor(entity);
		mcoAchadoDAO.merge(entity);
	}

	public Long pesquisarRegioesAnatomicasAtivasPorDescCount(String param) {
		return this.exameService.pesquisarRegioesAnatomicasAtivasPorDescricaoSeqCount(param);
	}
	
	public Long pesquisarAchadosCount(AchadoVO filtro) {
		return this.mcoAchadoDAO.buscarAchadosPorDescSituacaoCount(filtro.getDescricaoAcd(), filtro.getSituacao(), filtro.getSeqRan());
	}
}
