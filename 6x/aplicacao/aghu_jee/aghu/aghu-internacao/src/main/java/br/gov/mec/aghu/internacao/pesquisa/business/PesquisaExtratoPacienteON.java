package br.gov.mec.aghu.internacao.pesquisa.business;

import java.math.BigDecimal;
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

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.VAinMovimentosExtratoDAO;
import br.gov.mec.aghu.internacao.pesquisa.vo.DadosInternacaoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ExtratoPacienteVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class PesquisaExtratoPacienteON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaExtratoPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IPacienteFacade pacienteFacade;

@Inject
private VAinMovimentosExtratoDAO vAinMovimentosExtratoDAO;

@EJB
private IAghuFacade aghuFacade;

@EJB
private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4606405712775991200L;

	public boolean validar(Integer prontuario) throws ApplicationBusinessException {
		if (prontuario == null) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public List<ExtratoPacienteVO> pesquisarExtratoPaciente(Integer firstResult, Integer maxResult, Integer codigoInternacao,
			Date dataInternacao) {

		List<ExtratoPacienteVO> retorno = new ArrayList<ExtratoPacienteVO>();
		List<Object[]> res = getVAinMovimentosExtratoDAO().pesquisarExtratoPaciente(firstResult, maxResult, codigoInternacao,
				dataInternacao);

		// Criando lista de VO.
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();
			ExtratoPacienteVO vo = new ExtratoPacienteVO();

			if (obj[0] != null) {
				vo.setDataLancamento(dateFormat.format((Date) obj[0]));
			}
			if (obj[1] != null) {
				vo.setTipoMovimento(this.getDescricaoTipoMovtoInternacao((Byte) obj[1]));
			}
			if (obj[2] != null) {
				final AghEspecialidades especialidade = this.getCadastrosBasicosInternacaoFacade().obterEspecialidade( ((BigDecimal) obj[2]).shortValue());
				vo.setEspecialidade(especialidade.getSigla());
				vo.setDescEspecialidade(especialidade.getNomeEspecialidade());
			}
			if (obj[3] != null) {
				vo.setLeito((String) obj[3]);
			}
			if (obj[4] != null) {
				vo.setQuarto(((String) obj[4]));
			}
			if (obj[5] != null) {
				vo.setUnidade(this.getDescricaoUnidadeFuncional((Short) obj[5]));
			}
			if (obj[6] != null && obj[7] != null) {
				vo.setNomeInformante(this.getNomeServidor((Integer) obj[7], (Short) obj[6]));
			}
			if (obj[8] != null && obj[9] != null) {
				vo.setCrmMedico(this.getNroRegistroConselho(((BigDecimal) obj[8]).intValue(), ((BigDecimal) obj[9]).shortValue()));
				vo.setNomeMedico(this.getNomeServidor(((BigDecimal) obj[8]).intValue(), ((BigDecimal) obj[9]).shortValue()));
			}
			if (obj[10] != null) {
				vo.setCriadoEm(dateFormat.format((Date) obj[10]));
			}
			retorno.add(vo);
		}
		return retorno;
	}

	public Long pesquisarExtratoPacienteCount(Integer codigoInternacao, Date dataInternacao) {
		return getVAinMovimentosExtratoDAO().pesquisarExtratoPacienteCount(codigoInternacao, dataInternacao);
	}

	public String getNomePacienteProntuario(Integer prontuario) {
		return getPacienteFacade().obterNomePacientePorProntuario(prontuario);
	}

	public List<DadosInternacaoVO> pesquisarDatas(Integer prontuario, Date dataInternacao) {

		List<Object[]> res = getInternacaoFacade().pesquisarDatas(prontuario, dataInternacao);

		List<DadosInternacaoVO> retorno = new ArrayList<DadosInternacaoVO>();

		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();
			DadosInternacaoVO intVO = new DadosInternacaoVO();
			if (obj[0] != null) {
				intVO.setDataInternacao((Date) obj[0]);
			}
			if (obj[1] != null) {
				intVO.setCodigoConvenioPlano((Short) obj[1]);
			}
			if (obj[2] != null) {
				intVO.setCodigoPlanoSaude((Byte) obj[2]);
			}
			StringBuffer convenioPlano = new StringBuffer();
			if (obj[3] != null) {
				convenioPlano.append((String) obj[3]);
			}
			if (obj[4] != null) {
				convenioPlano.append(" - ").append((String) obj[4]);
			}
			intVO.setDescricaoConvenioPlanoSaude(convenioPlano.toString());
			if (obj[5] != null) {
				intVO.setCodigoInternacao((Integer) obj[5]);
			}
			retorno.add(intVO);
		}
		return retorno;
	}

	public String getNroRegistroConselho(Integer matricula, Short vinculo) {
		return getRegistroColaboradorFacade().obterPrimeiroNroRegistroConselho(matricula, vinculo);
	}

	public String getNomeServidor(Integer matricula, Short vinculo) {
		return getRegistroColaboradorFacade().obterNomeServidor(matricula, vinculo);
	}

	public String getDescricaoTipoMovtoInternacao(Byte codigo) {
		return getCadastrosBasicosInternacaoFacade().obterDescricaoTipoMovtoInternacao(codigo);
	}

	public String getDescricaoUnidadeFuncional(Short codigo) {
		return getAghuFacade().obterDescricaoUnidadeFuncional(codigo);
	}

	protected VAinMovimentosExtratoDAO getVAinMovimentosExtratoDAO() {
		return vAinMovimentosExtratoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
