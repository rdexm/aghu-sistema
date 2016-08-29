package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.McoPlacar;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.perinatologia.dao.McoPlacarDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoSnappesDAO;
import br.gov.mec.aghu.perinatologia.vo.RelatorioSnappeVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PerinatologiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PerinatologiaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@Inject
	private McoPlacarDAO mcoPlacarDAO;
	
	@Inject
	private McoSnappesDAO mcoSnappesDAO;
	
	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
		
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -5478728843790745504L;

	public void persistirPlacar(McoPlacar placar) {
		// TODO Implementar triggers de Insert e Update da tabela MCO_PLACARS. Por enquanto estas triggers
		//estão executando no Oracle. Quando as mesmas forem implementadas será preciso chaveá-las no banco
		this.getMcoPlacarDAO().persistirPlacar(placar);
	}
	
	private McoPlacarDAO getMcoPlacarDAO() {
		return mcoPlacarDAO;
	}

	/**
	 * Retorna os registros do paciente informado
	 * 
	 * @param codigoPaciente
	 */
	public McoPlacar buscarPlacar(Integer codigoPaciente) {
		return this.getMcoPlacarDAO().buscarPlacar(codigoPaciente);
	}
	
	public void persistirProcedimentoReanimacao(){
		// buscar componente sanguineo pelo seq
		// buscar medicamento pelo seq
		
	}

	public List<RelatorioSnappeVO> listarRelatorioSnappe2(Integer pacCodigoRecemNascido, Short seqp, Integer gsoPacCodigo, Short gsoSeqp, Byte seqpRecemNascido) throws ApplicationBusinessException {
		List<RelatorioSnappeVO> lista = new ArrayList<RelatorioSnappeVO>();
		McoRecemNascidos recemNascido = null;
		RelatorioSnappeVO item = new RelatorioSnappeVO();
		item = mcoSnappesDAO.obterRegistroSnappeImpressao(pacCodigoRecemNascido, seqp);
		recemNascido = mcoRecemNascidosDAO.obterMcoRecemNascidosPorId(gsoPacCodigo, gsoSeqp, Integer.valueOf(seqpRecemNascido.toString()));
		
		if (item != null) {		
			BigDecimal peso = pacienteFacade.obterPesoPacientesPorCodigoPaciente(pacCodigoRecemNascido);
			String pesoNascimentoIdentificacao = null;
			if(peso != null){
				peso = peso.multiply(new BigDecimal(1000));
				pesoNascimentoIdentificacao = String.valueOf(peso.intValue()).concat(" gr");
				item.setPesoNascimentoIdentificacao(pesoNascimentoIdentificacao);
			}
			
			if (recemNascido != null && recemNascido.getDthrNascimento() != null) {
				item.setDataIdentificacao(DateUtil.obterDataFormatada(recemNascido.getDthrNascimento(), "dd/MM/yyyy HH:mm"));
			}	
				
			item.setProntuarioIdentificacao(CoreUtil.formataProntuario(item.getProntuario()));
			
			RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			BuscaConselhoProfissionalServidorVO conselhoProfissional = prontuarioOnlineFacade.buscaConselhoProfissionalServidorVO(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
			
			if (conselhoProfissional != null) {
				String infResponsavel = conselhoProfissional.getNome().concat(" CREMERS ").concat(conselhoProfissional.getNumeroRegistroConselho());
				item.setInfResponsavel(infResponsavel);
			}
			
			item.setIdadeIdentificacao(ambulatorioFacade.obterIdadeMesDias(item.getDataNascimento(), new Date()));
			lista.add(item);
		}
		return lista;
	}
	
}