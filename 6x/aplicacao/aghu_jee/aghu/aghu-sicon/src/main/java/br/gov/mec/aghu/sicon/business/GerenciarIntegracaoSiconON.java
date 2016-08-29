package br.gov.mec.aghu.sicon.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.sicon.dao.ScoAditContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoResContratoDAO;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@SuppressWarnings({"PMD.AtributoEmSeamContextManager"})
@Stateless
public class GerenciarIntegracaoSiconON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(GerenciarIntegracaoSiconON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoResContratoDAO scoResContratoDAO;

@Inject
private ScoAditContratoDAO scoAditContratoDAO;

@Inject
private ScoContratoDAO scoContratoDAO;

	private static final long serialVersionUID = 9201153394582308593L;
	private ScoContratoDAO contratoDAO;
	private ScoAditContratoDAO aditContratoDAO;
	private ScoResContratoDAO resContratoDAO;

	/**
	 * Pesquisa os contratos por filtro de contratos
	 */
	public List<ScoContrato> listarContratosFiltro(
			ContratoFiltroVO filtroContratoIntegracao) {

		List<ScoContrato> listContratos = scoContratoDAO.listarContratosFiltro(filtroContratoIntegracao);

		if (listContratos != null && listContratos.size() > 0) {
			// Ajustar vigência final de contrato por aditivo(s)
			return this.ajustarDadosDosContratos(listContratos);
		}

		return listContratos;
	}

	/**
	 * Pesquisa os aditivos dos contratos informados
	 */
	public List<ScoAditContrato> listarAditivosContrato(
			List<ScoContrato> listContratos) {
		return getAditContratoDAO().listarAditivosContrato(listContratos);
	}

	/**
	 * Pesquisa as rescisões dos contratos informados
	 */
	public List<ScoResContrato> listarRescisoesContrato(
			List<ScoContrato> listContratos) {
		return scoResContratoDAO.listarRescisoesContrato(listContratos);
	}

	/**
	 * Pesquisa os aditivos por vigencias e situação de envio de aditivo
	 */
	public List<ScoAditContrato> pesquisarAditivos(
			ContratoFiltroVO filtroContratoIntegracao) {
		return scoAditContratoDAO.pesquisarAditivos(
				filtroContratoIntegracao);
	}

	/**
	 * Pesquisa as rescisões dos contratos e conforme filtro de rescisão.
	 */
	public List<ScoResContrato> listarRescisoesContratoFiltro(
			List<ScoContrato> listContratos,
			ContratoFiltroVO filtroContratoIntegracao) {
		return getResContratoDAO().listarRescisoesContratoFiltro(listContratos,
				filtroContratoIntegracao);
	}

	public List<ScoResContrato> pesquisarRescisoes(
			ContratoFiltroVO filtroContratoIntegracao) {
		
		if(filtroContratoIntegracao != null &&
				filtroContratoIntegracao.getContrato() != null &&
					filtroContratoIntegracao.getContrato().getNrContrato() != null){
			ScoContrato contrato = getContratoDAO().obterContratoPorNumeroContrato(filtroContratoIntegracao.getContrato().getNrContrato());
			if (contrato != null){
				filtroContratoIntegracao.getContrato().setSeq(contrato.getSeq());
			}
		}
		return getResContratoDAO().pesquisarRescisoes(filtroContratoIntegracao);
	}

	public List<ScoResContrato> listarRescisoesSituacaoEnvio(
			DominioSituacaoEnvioContrato situacao) {
		return getResContratoDAO().listarRescisoesSituacaoEnvio(situacao);
	}

	/**
	 * Ajusta a visão da data de fim vigência de contratos conforme a data de
	 * vigência final de seu aditivos mais atual
	 */
	/**
	 * @param listContratos
	 * @return
	 */
	public List<ScoContrato> ajustarDadosDosContratos(
			List<ScoContrato> listContratos) {

		List<ScoContrato> listRetorno = new ArrayList<ScoContrato>();

		List<ScoAditContrato> listAditivosContratos = this
				.listarAditivosContrato(listContratos);

		for (ScoContrato contrato : listContratos) {
			
			//inicializada as bags
			Hibernate.initialize(contrato.getAditivos());
			Hibernate.initialize(contrato.getItensContrato());
			Hibernate.initialize(contrato.getScoAfContratos());
			for(ScoAfContrato afContrato : contrato.getScoAfContratos()){
				Hibernate.initialize(afContrato.getScoAutorizacoesForn());
				Hibernate.initialize(afContrato.getScoAutorizacoesForn().getItensAutorizacaoForn());
			}

			if (listAditivosContratos != null
					&& listAditivosContratos.size() > 0) {

				List<ScoAditContrato> listAditivos = this
						.obterAditivosAlteracaoVigencia(listAditivosContratos,
								contrato.getSeq());

				// Aditivos com alteração de vigência
				if (listAditivos != null && listAditivos.size() > 0) {

					ScoAditContrato aditivoSelec = listAditivos.get(0);
					Date criadoEm_Aux = aditivoSelec.getCriadoEm();

					// Seleciona o último aditivo inserido (mais atual)
					for (ScoAditContrato aditivo : listAditivos) {
						if (aditivo.getCriadoEm().after(criadoEm_Aux)) {
							criadoEm_Aux = aditivo.getCriadoEm();
							aditivoSelec = aditivo;
						}
					}

			
					contrato.setDataFimVigComAditivos(aditivoSelec.getDtFimVigencia());
				}

				listRetorno.add(contrato);

				// Contrato sem aditivo(s), assume a data de vigência original
				// do contrato
			} else {
				listRetorno.add(contrato);
			}

		}

		return listRetorno;
	}

	/**
	 * Seleciona os aditivos com alteração de vigência para um contrato.
	 */
	@SuppressWarnings("unchecked")
	private List<ScoAditContrato> obterAditivosAlteracaoVigencia(
			List<ScoAditContrato> aditivos, final Integer cont_seq) {

		List<ScoAditContrato> listAditivos = null;

		listAditivos = (List<ScoAditContrato>) CollectionUtils.select(aditivos,
				new Predicate() {
					@Override
					public boolean evaluate(Object p) {
						ScoAditContrato a = (ScoAditContrato) p;
						return (a.getDtInicioVigencia() != null
								&& a.getDtFimVigencia() != null && a.getCont()
								.getSeq() == cont_seq);
					}
				});

		return listAditivos;
	}

	protected ScoContratoDAO getContratoDAO() {

		if (this.contratoDAO != null) {
			return contratoDAO;
		} else {
			return scoContratoDAO;
		}
	}

	protected ScoAditContratoDAO getAditContratoDAO() {

		if (this.aditContratoDAO != null) {
			return aditContratoDAO;
		} else {
			return scoAditContratoDAO;
		}
	}

	protected ScoResContratoDAO getResContratoDAO() {

		if (this.resContratoDAO != null) {
			return resContratoDAO;
		} else {
			return scoResContratoDAO;
		}
	}
	
}
