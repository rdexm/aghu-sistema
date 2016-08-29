package br.gov.mec.aghu.perinatologia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.McoSnappes;
import br.gov.mec.aghu.model.McoTabAdequacaoPeso;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.perinatologia.business.RegistrarGestacaoAbaExtFisicoRNON.RegistrarGestacaoAbaExtFisicoRNONExceptionCode;
import br.gov.mec.aghu.perinatologia.dao.McoSnappesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTabAdequacaoPesoDAO;
import br.gov.mec.aghu.perinatologia.vo.SnappeElaboradorVO;
import br.gov.mec.aghu.perinatologia.vo.TabAdequacaoPesoPercentilVO;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class SnappeON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7873568159804634990L;
	
	private static final Log LOG = LogFactory.getLog(SnappeON.class);

	@Inject
	private McoTabAdequacaoPesoDAO mcoTabAdequacaoPesoDAO;
	
	@Inject
	private IPacienteService pacienteService;
	@Inject
	private McoSnappesDAO mcoSnappesDAO;
	
	@Inject
	private IRegistroColaboradorService  registroService; 
	
	@Inject
	private SnappeRN mcoSnappeRN;
	
	private final String OPERADOR_MAIOR = ">";

	@Override
	protected Log getLogger() {
		return LOG;
	}
	

	public enum SnappeONExceptionCode implements BusinessExceptionCode {
		MCO_00140, MENSAGEM_SERVICO_INDISPONIVEL, PACIENTE_SUMARIO_ALTA, 

	}
	
	/**
	 * #27490 ON-06 listar a tabela de adequação
	 * @return
	 */
	public List<TabAdequacaoPesoPercentilVO> listaTabAdequacaoPeso(){
		List<McoTabAdequacaoPeso> listaTabAdqPeso = mcoTabAdequacaoPesoDAO.obterListaMcoTabAdequacaoPesoComPencentil3NotNull();
		List<TabAdequacaoPesoPercentilVO> listaVO = new ArrayList<TabAdequacaoPesoPercentilVO>();
		if (isNotListaVazia(listaTabAdqPeso)) {
			int controleUltimoRegistro = 0;
			int ultimoRegistroLista = listaTabAdqPeso.size() - 1 ;
			for (McoTabAdequacaoPeso tabAdequacaoPeso : listaTabAdqPeso) {
				TabAdequacaoPesoPercentilVO vo = new TabAdequacaoPesoPercentilVO();
				vo.setPercentil3(tabAdequacaoPeso.getPercentil3());
				if (controleUltimoRegistro == ultimoRegistroLista) {
					vo.setIgSemanas(OPERADOR_MAIOR + tabAdequacaoPeso.getIgSemanas());
				} else {
					vo.setIgSemanas(tabAdequacaoPeso.getIgSemanas().toString());
				}
				controleUltimoRegistro++;
				listaVO.add(vo);
			}
		}
		return listaVO;
	}
	
	@SuppressWarnings("rawtypes")
	private Boolean isNotListaVazia(List lista) {
		return lista != null && !lista.isEmpty();
	}
	
	
	/**
	 * #27490 ON-01 e ON-02
	 * @param pacCodigo
	 * @throws ApplicationBusinessException
	 */
	public List<SnappeElaboradorVO>  listaSnappe(Integer pacCodigo) throws ApplicationBusinessException{
		Paciente paciente = obterPacientePorCodigo(pacCodigo);
		List<SnappeElaboradorVO> listaElaboradorVO = new ArrayList<SnappeElaboradorVO>();
		if (paciente == null) {
			throw new ApplicationBusinessException(SnappeONExceptionCode.MCO_00140);
		}
		List<McoSnappes> listaSnappes = mcoSnappesDAO.listarSnappesPorCodigoPaciente(pacCodigo);
		if (isNotListaVazia(listaSnappes)) {
			for (McoSnappes mcoSnappes : listaSnappes) {
				Servidor servidor = obterVRapPessoaServidorPorVinCodigoMatricula(mcoSnappes.getSerMatricula(), mcoSnappes.getSerVinCodigo());
				if (servidor != null) {
					SnappeElaboradorVO elaboradorVO = new SnappeElaboradorVO();
					elaboradorVO.setId(mcoSnappes.getId());
					elaboradorVO.setCriadoEm(mcoSnappes.getCriadoEm());
					elaboradorVO.setSerMatricula(mcoSnappes.getSerMatricula());
					elaboradorVO.setSerVinCodigo(mcoSnappes.getSerVinCodigo());
					elaboradorVO.setNome(servidor.getNomePessoaFisica());
					listaElaboradorVO.add(elaboradorVO);
				}
			}
		}
		return listaElaboradorVO;
	}
	

	/**
	 * #27490 ON03 - Verificar se teve alteração na tela
	 * @param snappe
	 * @return
	 */
	public boolean verificarAtualizacao(McoSnappes snappe){
		McoSnappes snappeOriginal = mcoSnappesDAO.obterOriginal(snappe.getId());
		if (snappeOriginal != null) {
			return mcoSnappeRN.verificarAtualizacoes(snappe, snappeOriginal);
		}
		return false;
	}
	
	/**
	 * #27490 - ON04 Verificar se o usuário que alterou é o mesmo que incluiu
	 * 
	 * @param snappe
	 * @return
	 */
	public boolean verificarUsuarioAlteracao(McoSnappes snappe) {
		return mcoSnappeRN.verificarUsuarioAlteracao(snappe);
	}

	private Paciente obterPacientePorCodigo(Integer pacCodigo) throws ApplicationBusinessException {
		Paciente result = null;
		PacienteFiltro filtro = new PacienteFiltro();
		filtro.setCodigo(pacCodigo);
		try {
			result = pacienteService.obterPacientePorCodigoOuProntuario(filtro);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaExtFisicoRNONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	/**
	 * #27490  - ON -10 
	 * Verificar se sumária alta foi preenchido
	 * @param snappe
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarSumariaAlta(McoSnappes snappe) throws ApplicationBusinessException{
		if (snappe.getDthrSumarioAlta() != null) {
			throw new ApplicationBusinessException(SnappeONExceptionCode.PACIENTE_SUMARIO_ALTA);
		}
		return true;
	}
	private Servidor obterVRapPessoaServidorPorVinCodigoMatricula(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {
		Servidor result = null;
		try {
			result = registroService.obterVRapPessoaServidorPorVinCodigoMatricula(matricula, vinCodigo);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(SnappeONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;

	}
	
}
