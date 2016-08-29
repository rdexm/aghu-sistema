package br.gov.mec.aghu.internacao.leitos.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.VAinLeitosLimpezaDAO;
import br.gov.mec.aghu.internacao.leitos.vo.LiberaLeitoLimpezaVO;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.VAinLeitosLimpeza;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class LiberaLeitoLimpezaON extends BaseBusiness {


@EJB
private LiberaLeitoLimpezaRN liberaLeitoLimpezaRN;

private static final Log LOG = LogFactory.getLog(LiberaLeitoLimpezaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private VAinLeitosLimpezaDAO vAinLeitosLimpezaDAO;

@Inject
private AinLeitosDAO ainLeitosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5693485283579247614L;
	private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	/**
	 * Método que obtém a lista de leitos limpeza.
	 * 
	 * @dbtables VAinLeitosLimpeza select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @return
	 */
	public List<LiberaLeitoLimpezaVO> pesquisarLeitosLimpeza(String leito,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<LiberaLeitoLimpezaVO> retorno = new ArrayList<LiberaLeitoLimpezaVO>();
		
		List<VAinLeitosLimpeza> result = getVAinLeitosLimpezaDAO().pesquisarLeitosLimpeza(leito, firstResult, maxResult, orderProperty, asc);

		for (VAinLeitosLimpeza view : result) {
			if (view != null) {
				LiberaLeitoLimpezaVO vo = new LiberaLeitoLimpezaVO();

				vo.setLtoId(view.getId().getLtoId());
				vo.setAndarAlaDescricao(view.getAndarAlaDescricao());
				vo.setSituacao(obterSituacaoLeito(view.getId().getLtoId()));

				if (view.getIntSeq() != null) {
					vo.setIntSeq(view.getIntSeq());
				}

				Date dataBloqueio = getLiberaLeitoLimpezaRN()
						.recuperarDataBloqueio(vo.getLtoId());
				if (dataBloqueio != null) {
					vo.setDataBloqueio(format.format(dataBloqueio));
				}

				retorno.add(vo);
			}
		}

		return retorno;
	}

	/**
	 * Método que obtém a lista de leitos limpeza para geração do relatório.
	 * 
	 * @dbtables VAinLeitosLimpeza select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @return
	 */
	public List<LiberaLeitoLimpezaVO> pesquisarLeitosLimpeza() {

		List<LiberaLeitoLimpezaVO> retorno = new ArrayList<LiberaLeitoLimpezaVO>();
		
		List<VAinLeitosLimpeza> result = getVAinLeitosLimpezaDAO().pesquisarLeitosOrderByIdAndar();
		
		for (VAinLeitosLimpeza view : result) {
			if (view != null) {
				LiberaLeitoLimpezaVO vo = new LiberaLeitoLimpezaVO();
	
				vo.setLtoId(view.getId().getLtoId());
				vo.setAndarAlaDescricao(view.getAndarAlaDescricao());
				vo.setSituacao(obterSituacaoLeito(view.getId().getLtoId()));
	
				if (view.getIntSeq() != null) {
					vo.setIntSeq(view.getIntSeq());
				}
	
				Date dataBloqueio = getLiberaLeitoLimpezaRN().recuperarDataBloqueio(vo
						.getLtoId());
				if (dataBloqueio != null) {
					vo.setDataBloqueio(format.format(dataBloqueio));
					vo.setDataBloqueioOrdenacao(dataBloqueio);
				}
				retorno.add(vo);
			}
		}

		Collections.sort(retorno, new Comparator<LiberaLeitoLimpezaVO>() {
			@Override
			public int compare(LiberaLeitoLimpezaVO o1, LiberaLeitoLimpezaVO o2) {
				return o1.getDataBloqueioOrdenacao().compareTo(
						o2.getDataBloqueioOrdenacao());
			}
		});

		return retorno;
	}

	/**
	 * Método que obtém o count da lista de leitos limpeza.
	 * 
	 * @dbtables VAinLeitosLimpeza select
	 * 
	 * @param leito
	 * @return
	 */
	public Long pesquisarLeitosLimpezaCount(String leito) {
		return getVAinLeitosLimpezaDAO().pesquisarLeitosLimpezaCount(leito);
	}

	/**
	 * Método responsável pela liberação do leito.
	 * 
	 * @dbtables AghParametros select
	 * @dbtables AinInternacao select
	 * @dbtables AipPacientes select
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AinLeitos select,insert,update
	 * @dbtables AinQuartos select,insert,update
	 * @dbtables AinExtratoLeitos select,insert,update
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AghCaractUnidFuncionais select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @throws ApplicationBusinessException
	 */
	public void liberarLeitoLimpeza(AinLeitos leito)
			throws ApplicationBusinessException {
		this.getLiberaLeitoLimpezaRN().liberaLeitoLimpeza(leito);
	}

	/**
	 * Método responsável pelo bloqueio do leito. TODO: Este método é restrito
	 * apenas a usuarios com perfil AING_SUPERV_HIGIEN_LEITO permissão para
	 * pesquisa. Lembrar de incluir esse mapeamento de segurança.
	 * 
	 * @dbtables AghParametros select
	 * @dbtables AinInternacao select
	 * @dbtables AipPacientes select
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AinLeitos select,insert,update
	 * @dbtables AinQuartos select,insert,update
	 * @dbtables AinExtratoLeitos select,insert,update
	 * @dbtables AinTiposMovimentoLeito select
	 * @dbtables AghCaractUnidFuncionais select
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @throws ApplicationBusinessException
	 */
	public void bloquearLeitoLimpeza(AinLeitos leito, AinInternacao internacao)
			throws ApplicationBusinessException {
		this.getLiberaLeitoLimpezaRN().bloquearLeitoLimpeza(leito, internacao);
	}
	
	/**
	 * Retorna a situação do leito
	 * 
	 * @dbtables AinLeitos select
	 * 
	 * @param leito
	 * @return
	 */
	private String obterSituacaoLeito(String leito) {
		return getAinLeitosDAO().obterSituacaoLeitoDescricao(leito);
	}

	protected LiberaLeitoLimpezaRN getLiberaLeitoLimpezaRN(){
		return liberaLeitoLimpezaRN;
	}
	
	protected VAinLeitosLimpezaDAO getVAinLeitosLimpezaDAO(){
		return vAinLeitosLimpezaDAO;
	}
	
	protected AinLeitosDAO getAinLeitosDAO(){
		return ainLeitosDAO;
	}
}
