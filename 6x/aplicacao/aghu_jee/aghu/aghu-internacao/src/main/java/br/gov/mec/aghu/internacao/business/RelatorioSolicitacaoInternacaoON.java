package br.gov.mec.aghu.internacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.internacao.dao.AinSolicitacoesInternacaoDAO;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.RelatorioSolicitacaoInternacaoVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioSolicitacaoInternacaoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioSolicitacaoInternacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinSolicitacoesInternacaoDAO ainSolicitacoesInternacaoDAO;

@EJB
private IPacienteFacade pacienteFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2150492827813588988L;

	public List<RelatorioSolicitacaoInternacaoVO> obterSolicitacoesInternacao(Date criadoEm,
			DominioSituacaoSolicitacaoInternacao indSitSolicInternacao, AghClinicas clinica, Date dtPrevistaInternacao,
			AipPacientes paciente, ServidorConselhoVO crm, AghEspecialidades especialidade, ConvenioPlanoVO convenio)
			throws ApplicationBusinessException {
		List<Object[]> res = this.getAinSolicitacoesInternacaoDAO().listarInformacoesSolicitacoesInternacao(criadoEm,
				indSitSolicInternacao, clinica, dtPrevistaInternacao, paciente, crm, especialidade, convenio);

		List<RelatorioSolicitacaoInternacaoVO> lista = new ArrayList<RelatorioSolicitacaoInternacaoVO>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Iterator<Object[]> it = res.iterator();

		while (it.hasNext()) {

			Object[] obj = it.next();

			RelatorioSolicitacaoInternacaoVO vo = new RelatorioSolicitacaoInternacaoVO();

			// cod_paciente
			if (obj[0] != null) {
				vo.setCodigo(obj[0].toString());
				//Idade do paciente
				AipPacientes pacienteRelatorio = getPacienteFacade().obterPacientePorCodigo(Integer.valueOf(vo.getCodigo()));
				vo.setIdade(pacienteRelatorio.getIdadeFormat());	
			}

			// nome_paciente
			if (obj[1] != null) {
				vo.setNome(obj[1].toString());
			}

			// prontuario
			if (obj[2] != null) {
				String prontAux = (obj[2]).toString();
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)
						+ "/" + prontAux.charAt(prontAux.length() - 1));

			}

			// data_prevista_internacao
			if (obj[3] != null) {
				vo.setPrevInternacao(sdf.format(obj[3]));
			}

			// especialidade_sigla
			if (obj[4] != null) {
				vo.setSigla(obj[4].toString());
			}
			
			// procedimento (ssm)
			if (obj[5] != null) {
				vo.setSsm(obj[5].toString() + " - " + obj[6].toString());
			}

			lista.add(vo);
		}

		return lista;
	}
	
	private IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
	
	protected AinSolicitacoesInternacaoDAO getAinSolicitacoesInternacaoDAO() {
		return ainSolicitacoesInternacaoDAO;
	}
	
}
