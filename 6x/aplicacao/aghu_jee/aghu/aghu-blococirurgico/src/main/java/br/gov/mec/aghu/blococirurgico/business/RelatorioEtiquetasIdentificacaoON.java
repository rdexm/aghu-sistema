package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.vo.RelatorioEtiquetasIdentificacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.VAghUnidFuncionalVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioEtiquetasIdentificacaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioEtiquetasIdentificacaoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private IPrescricaoMedicaFacade iPrescricaoMedicaFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = 3915969063673646303L;

	private enum RelatorioEtiquetasIdentificacaoONExceptonCode implements BusinessExceptionCode {
		MSG_CAMPOS_OBRIGATORIOS_ETIQUETAS_IDENTIFICACAO, MSG_CAMPOS_OBRIGATORIOS_ETIQUETAS_IDENTIFICACAO_2;
	}

	@SuppressWarnings("unchecked")
	public List<RelatorioEtiquetasIdentificacaoVO> pesquisarRelatorioEtiquetasIdentificacao(Short unfSeq, Short unfSeqMae, Boolean pacientesQueNaoPossuemPrevisaoAlta, Integer pacCodigoFonetica,
			Date dataCirurgia) {
		List<RelatorioEtiquetasIdentificacaoVO> lista = getAghuFacade().pesquisarRelatorioEtiquetasIdentificacao(unfSeq, unfSeqMae, pacCodigoFonetica, dataCirurgia);
		List<RelatorioEtiquetasIdentificacaoVO> toReturn = new ArrayList<RelatorioEtiquetasIdentificacaoVO>();

		for (RelatorioEtiquetasIdentificacaoVO relatorioEtiquetasIdentificacaoVO : lista) {
			if (relatorioEtiquetasIdentificacaoVO.getNumeroSubConsulta().equals(1)) {

				// Filtrar por Unidade Funcional em Atendimento
				if (verificarUnidadeFuncionalAtendimentos(relatorioEtiquetasIdentificacaoVO.getUnfSeq()) > 0) {
					if (pacientesQueNaoPossuemPrevisaoAlta) {

						// Filtrar por pacientes que nao possuem previão de alta 
						if (getPrescricaoMedicaFacade().verificaPrevisaoAltaProxima(
								getAghuFacade().buscarAtendimentoPorSeq(relatorioEtiquetasIdentificacaoVO.getAtendimento()))) {
							toReturn.add(relatorioEtiquetasIdentificacaoVO);
						}

					} else {
						toReturn.add(relatorioEtiquetasIdentificacaoVO);
					}
				}

			} else {
				toReturn.add(relatorioEtiquetasIdentificacaoVO);
			}
		}
		
		final BeanComparator ltoLtoIdComparator = new BeanComparator(RelatorioEtiquetasIdentificacaoVO.Fields.LTO_LTO_ID.toString(), new NullComparator(true));
		Collections.sort(toReturn, ltoLtoIdComparator);

		return toReturn;
	}

	/**
	 * @throws ApplicationBusinessException
	 *             #27200 RN2
	 */
	public void preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(AghUnidadesFuncionais unidadeFuncional, Integer pacCodigoFonetica,
			VAghUnidFuncionalVO unidadeFuncionalMae, Date dataCirurgia, Boolean caracteristicaUnidadeCirurgica) throws ApplicationBusinessException {
		if (unidadeFuncional == null && pacCodigoFonetica == null && unidadeFuncionalMae == null) {
			throw new ApplicationBusinessException(RelatorioEtiquetasIdentificacaoONExceptonCode.MSG_CAMPOS_OBRIGATORIOS_ETIQUETAS_IDENTIFICACAO);
		}
		if(dataCirurgia != null && caracteristicaUnidadeCirurgica != null && !caracteristicaUnidadeCirurgica){
			throw new ApplicationBusinessException(RelatorioEtiquetasIdentificacaoONExceptonCode.MSG_CAMPOS_OBRIGATORIOS_ETIQUETAS_IDENTIFICACAO_2);
		}
	}

	/**
	 * ORADB FUNCTION AINC_BUSCA_UNF_C_PAC
	 * 
	 * @param unfSeq
	 * @return
	 */
	public Short verificarUnidadeFuncionalAtendimentos(Short unfSeq) {
		return (getAghuFacade().verificarUnidadeFuncionalAtendimentos(unfSeq) ? unfSeq : 0);
	}

	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return iPrescricaoMedicaFacade;
	}

}
