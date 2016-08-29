package br.gov.mec.aghu.internacao.pesquisa.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;

@Stateless
public class PesquisaPacienteInternadoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaPacienteInternadoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;

@EJB
private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6623834019154543343L;

	/**
	 * Pesquisa internacoes de pacientes que possuam o atributo indSaidaPaciente
	 * == false e tambem pelos seguintes parametros:
	 * 
	 * @param prontuario
	 * @param pacCodigo
	 * @param pacNome
	 * @param espSeq
	 *            - id da especialidade
	 * @param ltoId
	 *            - id do leito
	 * @param qrtNum
	 *            - numero do quarto
	 * @param unfSeq
	 *            - id da unidade funcional
	 * @param matriculaProfessor
	 * @param vinCodigoProfessor
	 * 
	 * @return List<AinInternacao> - lista de internacoes conforme os criterios
	 *         de pesquisa
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacoesAtivas(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer prontuario, Integer pacCodigo, String pacNome, Short espSeq, String leitoID, Short qrtNum, Short unfSeq,
			Integer matriculaProfessor, Short vinCodigoProfessor) {

		return getAinInternacaoDAO().pesquisarInternacoesAtivas(firstResult, maxResults, orderProperty, asc, prontuario, pacCodigo,
				pacNome, espSeq, leitoID, qrtNum, unfSeq, matriculaProfessor, vinCodigoProfessor);
	}

	public Long pesquisarInternacoesAtivasCount(Integer prontuario, Integer pacCodigo, String pacNome, Short espSeq,
			String leitoID, Short qrtNum, Short unfSeq, Integer matriculaProfessor, Short vinCodigoProfessor) {

		return getAinInternacaoDAO().pesquisarInternacoesAtivasCount(prontuario, pacCodigo, pacNome, espSeq, leitoID, qrtNum, unfSeq,
				matriculaProfessor, vinCodigoProfessor);
	}

	/**
	 * ORADB Function AINC_BUSCA_UNID_INT.
	 * 
	 * @param leitoID
	 * @param qrtNumero
	 * @param unfSeq
	 * @return
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public Short buscarUnidadeInternacao(String leitoID, Short qrtNumero, Short unfSeq) {
		Short seqUnfRetorno = null;
		if (unfSeq != null) {
			seqUnfRetorno = unfSeq;
		} else if (qrtNumero != null) {
			AinQuartos quarto = getCadastrosBasicosInternacaoFacade().obterQuarto(qrtNumero);
			if (quarto != null && quarto.getUnidadeFuncional() != null) {
				seqUnfRetorno = quarto.getUnidadeFuncional().getSeq();
			}
		} else if (leitoID != null) {
			AinLeitos leito = getCadastrosBasicosInternacaoFacade().obterLeitoPorId(leitoID);
			if (leito != null && leito.getQuarto() != null && leito.getQuarto().getUnidadeFuncional() != null) {
				seqUnfRetorno = leito.getQuarto().getUnidadeFuncional().getSeq();
			}
		}
		return seqUnfRetorno;
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

}