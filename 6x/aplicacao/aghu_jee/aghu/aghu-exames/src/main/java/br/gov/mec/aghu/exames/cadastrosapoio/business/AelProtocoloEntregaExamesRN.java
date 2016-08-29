package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.exames.dao.AelItemEntregaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelProtocoloEntregaExamesDAO;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.ItensProtocoloVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.exames.pesquisa.vo.ResultadoPesquisaProtocoloVO;
import br.gov.mec.aghu.exames.vo.ProtocoloEntregaExamesVO;
import br.gov.mec.aghu.exames.vo.ProtocoloItemEntregaExamesVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelItemEntregaExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelProtocoloEntregaExames;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class AelProtocoloEntregaExamesRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelProtocoloEntregaExamesRN.class);
	private static final long serialVersionUID = -3963141278375599723L;
	private static final String DATA = "dd/MM/yyyy";
	private static final String HORARIO = "HH:mm";
	
	@Inject
	private AelProtocoloEntregaExamesDAO aelProtocoloEntregaExamesDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemEntregaExamesDAO aelItemEntregaExamesDAO;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade ;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
    public enum ProtocoloEntregaExamesRNExceptionCode implements BusinessExceptionCode {

        ERRO_LISTA_ITENS_NULA, PROTOCOLO_NULO;

    }
    
    public void persistirNovoProtocolo(AelProtocoloEntregaExames protocolo)  throws ApplicationBusinessException {

    	if (protocolo != null && (protocolo.getItemEntregaExames() != null)) {
    		Long seqAntigoProtocolo = protocolo.getSeq();
    		protocolo.setSeq(null);
    		this.getAelProtocoloEntregaExamesDAO().persistir(protocolo);
	    		persistirItens(protocolo, seqAntigoProtocolo);
    	} else {
            throw new ApplicationBusinessException(ProtocoloEntregaExamesRNExceptionCode.PROTOCOLO_NULO);
    	}
    }

	private void persistirItens(AelProtocoloEntregaExames protocolo, Long seqAntigoProtocolo) {
		List<AelItemSolicitacaoExames> itensEntregaExames = aelItemEntregaExamesDAO.recuperarItensPorNumeroProtocoloAntigo(seqAntigoProtocolo);
		for (AelItemSolicitacaoExames item : itensEntregaExames) {
			AelItemEntregaExames itemEntrega = new AelItemEntregaExames();
			itemEntrega.setSeq(null);
			itemEntrega.setProtocoloEntregaExames(protocolo);
			itemEntrega.setSolicitacaoExames(item);
			this.getAelItemEntregaExamesDAO().persistir(itemEntrega);
		}
	}

    public void persistirProtocolo(Map<Integer, Vector<Short>> listaItens, AelProtocoloEntregaExames protocolo) throws  ApplicationBusinessException {

    	if(listaItens != null) {
                for(Map.Entry<Integer, Vector<Short>> vetor : listaItens.entrySet()) {
                        for(Short item : vetor.getValue()) {
                          AelItemEntregaExames itemEntregaExames = new AelItemEntregaExames();
                          persistirProtocoloEntregaExames(protocolo, vetor, item, itemEntregaExames);
                        }
            }
    	} else {
            throw new ApplicationBusinessException(ProtocoloEntregaExamesRNExceptionCode.ERRO_LISTA_ITENS_NULA);
             }
    }

    private void persistirProtocoloEntregaExames(AelProtocoloEntregaExames protocolo, Map.Entry<Integer, Vector<Short>> vetor, Short item, AelItemEntregaExames itemEntregaExames) throws ApplicationBusinessException {

        if(protocolo != null) {
                  this.getAelProtocoloEntregaExamesDAO().persistir(protocolo);
                  getAelItemEntregaExamesDAO().flush();
                          List<AelItemSolicitacaoExames> itemSolicitacaoExames = recuperarListaSolicitacaoItens(vetor.getKey(), item);
                      persistirItensEntregaExames(protocolo, vetor, itemEntregaExames, itemSolicitacaoExames);
                      getAelItemEntregaExamesDAO().flush();
          } else {
                        throw new ApplicationBusinessException(ProtocoloEntregaExamesRNExceptionCode.PROTOCOLO_NULO);
                  }
    }

    private void persistirItensEntregaExames(AelProtocoloEntregaExames protocolo, Map.Entry<Integer, Vector<Short>> vetor, AelItemEntregaExames itemEntregaExames, List<AelItemSolicitacaoExames> itemSolicitacaoExames) {

        for(AelItemSolicitacaoExames solicitacaoExame : itemSolicitacaoExames) {
                  if(solicitacaoExame.getId().getSoeSeq().equals(vetor.getKey())) {
                          itemEntregaExames.setProtocoloEntregaExames(protocolo);
                          itemEntregaExames.setSolicitacaoExames(solicitacaoExame);
                  }
                  getAelItemEntregaExamesDAO().persistir(itemEntregaExames);
          }
    }
    
    public ProtocoloEntregaExamesVO recuperarNovoRelatorioEntregaExames(AelProtocoloEntregaExames protocolo, List<PesquisaExamesPacientesVO> listaPacientes) {
    	
    	List<ProtocoloItemEntregaExamesVO> protocoloItemEntregaExamesVO = new ArrayList<ProtocoloItemEntregaExamesVO>();
    	ProtocoloEntregaExamesVO protocoloEntregaExamesVO = new ProtocoloEntregaExamesVO();
    	AipPacientes paciente = recuperarPaciente(listaPacientes);
    	AacConsultas consulta = recuperarConsulta(listaPacientes);
    	
    	recuperarNovoProtocolo(protocolo, protocoloEntregaExamesVO, paciente, consulta);
    	recuperarDataHorarioEntrega(protocolo, protocoloEntregaExamesVO);
		protocoloEntregaExamesVO.setProtocoloItemEntregaExamesVO(recuperarItemEntregaNovoProtocolo(protocolo, protocoloItemEntregaExamesVO, paciente));
		
    	return protocoloEntregaExamesVO;
    }

	private List<ProtocoloItemEntregaExamesVO> recuperarItemEntregaNovoProtocolo(AelProtocoloEntregaExames protocolo,	List<ProtocoloItemEntregaExamesVO> protocoloItemEntregaExamesVO, AipPacientes paciente) {
		
		List<AelItemEntregaExames> listaItemProtocolo = aelProtocoloEntregaExamesDAO.buscarItensProtocolo(protocolo.getSeq());
		
		for (AelItemEntregaExames itemEntregaExames : listaItemProtocolo) {
			
			ProtocoloItemEntregaExamesVO itemProtocolo = new ProtocoloItemEntregaExamesVO();
				itemProtocolo.setNomePaciente(verificarNomePaciente(paciente));
				itemProtocolo.setDescricaoExame(verificarDescricaoExameNovoProtocolo(itemEntregaExames));
				itemProtocolo.setSituacao(verificarSituacaoNovoProtocolo(itemEntregaExames));
				itemProtocolo.setSolicitacao(verificarSolicitacaoNovoProtocolo(itemEntregaExames));
				itemProtocolo.setSolicitante(verificarSolicitanteNovoProtocolo(itemEntregaExames));
				itemProtocolo.setUnidSolic(verificarUnidSolcNovoProtocolo(itemEntregaExames));
				itemProtocolo.setConvenio(verificarConvenioNovoProtocolo(itemEntregaExames));
				protocoloItemEntregaExamesVO.add(itemProtocolo);
		}
		return protocoloItemEntregaExamesVO;
		
	}

	private String verificarConvenioNovoProtocolo(AelItemEntregaExames itemEntregaExames) {
		return itemEntregaExames == null || itemEntregaExames.getSolicitacaoExames() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getConvenioSaudePlano() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getConvenioSaudePlano().getDescricao() == null 
				? "" : itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getConvenioSaudePlano().getDescricao();
	}

	private String verificarUnidSolcNovoProtocolo(AelItemEntregaExames itemEntregaExames) {
		return itemEntregaExames == null || itemEntregaExames.getSolicitacaoExames() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getUnidadeFuncional().getDescricao() == null 
				? "" : itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getUnidadeFuncional().getDescricao();
	}

	private String verificarSolicitanteNovoProtocolo(AelItemEntregaExames itemEntregaExames) {
		return itemEntregaExames == null || itemEntregaExames.getSolicitacaoExames() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getServidor() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getServidor().getUsuario() == null 
				? "" : itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getServidor().getUsuario();
	}

	private String verificarSolicitacaoNovoProtocolo(AelItemEntregaExames itemEntregaExames) {
		return itemEntregaExames == null || itemEntregaExames.getSolicitacaoExames() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame() == null 
				|| itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getSeq() == null 
				? "" : itemEntregaExames.getSolicitacaoExames().getSolicitacaoExame().getSeq().toString();
	}

	private String verificarSituacaoNovoProtocolo(AelItemEntregaExames itemEntregaExames) {
		return itemEntregaExames ==null || itemEntregaExames.getSolicitacaoExames() == null 
				|| itemEntregaExames.getSolicitacaoExames().getExame() == null 
				|| itemEntregaExames.getSolicitacaoExames().getExame().getIndSituacao().getDescricao() == null 
				? "" : itemEntregaExames.getSolicitacaoExames().getExame().getIndSituacao().getDescricao();
	}

	private String verificarDescricaoExameNovoProtocolo(AelItemEntregaExames itemEntregaExames) {
		return itemEntregaExames == null || itemEntregaExames.getSolicitacaoExames() == null 
				|| itemEntregaExames.getSolicitacaoExames().getExame() == null 
				|| itemEntregaExames.getSolicitacaoExames().getExame().getDescricao() == null  
				? "" : itemEntregaExames.getSolicitacaoExames().getExame().getDescricao();
	}

	private void recuperarNovoProtocolo(AelProtocoloEntregaExames protocolo, ProtocoloEntregaExamesVO protocoloEntregaExamesVO,	AipPacientes paciente, AacConsultas consulta) {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		protocoloEntregaExamesVO.setProntuario(verificarProntuario(paciente));
		protocoloEntregaExamesVO.setIdade(verificarIdade(paciente));
		protocoloEntregaExamesVO.setNomePaciente(verificarNomePaciente(paciente));
		protocoloEntregaExamesVO.setCodigoProtocolo(verificarCodigoProtocoloNovoProtocolo(protocolo));
		protocoloEntregaExamesVO.setNomeResponsavelRetirada(protocolo.getNomeResponsavelRetirada());
    	protocoloEntregaExamesVO.setCpf(protocolo.getCpf().toString());
    	protocoloEntregaExamesVO.setUsuarioResponsavel(servidorLogado.getServidor().getUsuario());
		protocoloEntregaExamesVO.setUnidadeSolicitacao(verificarUnidadeSolicitacao(consulta));
	}

	private String verificarCodigoProtocoloNovoProtocolo(AelProtocoloEntregaExames protocolo) {
		return protocolo == null || protocolo.getSeq() == null ? "" : protocolo.getSeq().toString();
	}
    
    public ProtocoloEntregaExamesVO recuperarRelatorioEntregaExames(Map<Integer, Vector<Short>> listaItens, AelProtocoloEntregaExames protocolo, List<PesquisaExamesPacientesVO> listaPacientes) {
    
    	ProtocoloEntregaExamesVO protocoloEntregaExamesVO = new ProtocoloEntregaExamesVO();
    	List<ProtocoloItemEntregaExamesVO> protocoloItemEntregaExamesVO = new ArrayList<ProtocoloItemEntregaExamesVO>();
    	List<AelItemSolicitacaoExames> itemSolicitacaoExames = new ArrayList<AelItemSolicitacaoExames>();
    	AipPacientes paciente = recuperarPaciente(listaPacientes);
    	itemSolicitacaoExames = recuperarItemSolicitacaoExames(listaItens, itemSolicitacaoExames);
    	
		popularProtocoloEntregaExamesVO(protocolo, protocoloEntregaExamesVO, paciente, listaPacientes);
		recuperarDataHorarioEntrega(protocolo, protocoloEntregaExamesVO);
    	
    	for(AelItemSolicitacaoExames itemSolicitacaoExame : itemSolicitacaoExames) {
    		ProtocoloItemEntregaExamesVO itemProtocolo = new ProtocoloItemEntregaExamesVO();
    		itemProtocolo.setNomePaciente(verificarNomePaciente(paciente));
    		itemProtocolo.setDescricaoExame(verificarDescricaoExame(itemSolicitacaoExame));
    		itemProtocolo.setSituacao(verificarSituacao(itemSolicitacaoExame));
    		itemProtocolo.setSolicitacao(verificarSolicitacao(itemSolicitacaoExame));
    		itemProtocolo.setSolicitante(verificarSolicitante(itemSolicitacaoExame));
    		itemProtocolo.setUnidSolic(verificarUnidadeSolicitante(itemSolicitacaoExame));
    		itemProtocolo.setConvenio(verificarConvenio(itemSolicitacaoExame));
    		protocoloItemEntregaExamesVO.add(itemProtocolo);
    	}
    	protocoloEntregaExamesVO.setProtocoloItemEntregaExamesVO(protocoloItemEntregaExamesVO);
		return protocoloEntregaExamesVO;
    }

	private String verificarUnidadeSolicitante(AelItemSolicitacaoExames itemSolicitacaoExame) {
		return itemSolicitacaoExame.getSolicitacaoExame() == null 
				|| itemSolicitacaoExame.getSolicitacaoExame().getUnidadeFuncional() == null 
				|| itemSolicitacaoExame.getSolicitacaoExame().getUnidadeFuncional().getDescricao() == null 
				? "" : itemSolicitacaoExame.getSolicitacaoExame().getUnidadeFuncional().getDescricao();
	}

	private String verificarSolicitante(AelItemSolicitacaoExames itemSolicitacaoExame) {
		return itemSolicitacaoExame.getSolicitacaoExame() == null 
				|| itemSolicitacaoExame.getSolicitacaoExame().getServidor() == null 
				|| itemSolicitacaoExame.getSolicitacaoExame().getServidor().getUsuario() == null 
				? "" : itemSolicitacaoExame.getSolicitacaoExame().getServidor().getUsuario();
	}

	private String verificarSolicitacao(AelItemSolicitacaoExames itemSolicitacaoExame) {
		return itemSolicitacaoExame.getSolicitacaoExame() == null 
				|| itemSolicitacaoExame.getSolicitacaoExame().getSeq() == null 
				? "" : itemSolicitacaoExame.getSolicitacaoExame().getSeq().toString();
	}

	private String verificarDescricaoExame(AelItemSolicitacaoExames itemSolicitacaoExame) {
		return itemSolicitacaoExame.getExame() == null 
				|| itemSolicitacaoExame.getExame().getDescricao() == null 
				? "" : itemSolicitacaoExame.getExame().getDescricao();
	}

	private void popularProtocoloEntregaExamesVO(AelProtocoloEntregaExames protocolo, ProtocoloEntregaExamesVO protocoloEntregaExamesVO, AipPacientes paciente, List<PesquisaExamesPacientesVO> listaPacientes) {
		
		AacConsultas consulta = recuperarConsulta(listaPacientes);
		
		protocoloEntregaExamesVO.setProntuario(verificarProntuario(paciente));
		protocoloEntregaExamesVO.setIdade(verificarIdade(paciente));
		protocoloEntregaExamesVO.setNomePaciente(verificarNomePaciente(paciente));
		protocoloEntregaExamesVO.setCodigoProtocolo(verificarCodigoProtocolo(protocolo));
		protocoloEntregaExamesVO.setNomeResponsavelRetirada(verificarNomeResponsavelRetirada(protocolo));
    	protocoloEntregaExamesVO.setCpf(verificarCPF(protocolo));
    	protocoloEntregaExamesVO.setUsuarioResponsavel(verificarUsuarioResponsavel(protocolo));
		protocoloEntregaExamesVO.setUnidadeSolicitacao(verificarUnidadeSolicitacao(consulta));
		
	}

	private String verificarProntuario(AipPacientes paciente) {
		return paciente.getProntuario() == null ? "" : paciente.getProntuario().toString();
	}

	private String verificarIdade(AipPacientes paciente) {
		return paciente.getIdadeFormat() == null ? "" : paciente.getIdadeFormat();
	}

	private String verificarNomePaciente(AipPacientes paciente) {
		return paciente.getNome() == null ? "" : paciente.getNome();
	}

	private String verificarCodigoProtocolo(AelProtocoloEntregaExames protocolo) {
		return protocolo.getSeq() == null ? "" : protocolo.getSeq().toString();
	}

	private String verificarNomeResponsavelRetirada(AelProtocoloEntregaExames protocolo) {
		return protocolo.getNomeResponsavelRetirada() == null ? "" : protocolo.getNomeResponsavelRetirada();
	}

	private String verificarCPF(AelProtocoloEntregaExames protocolo) {
		return protocolo.getCpf() == null ? "" : protocolo.getCpf().toString();
	}

	private String verificarUsuarioResponsavel(AelProtocoloEntregaExames protocolo) {
		return protocolo == null 
				|| protocolo.getServidor() == null 
				|| protocolo.getServidor().getUsuario() == null ? "" : protocolo.getServidor().getUsuario();
	}

	private String verificarConvenio(AelItemSolicitacaoExames solicitacao) {
		
		String convenio = solicitacao == null || solicitacao.getSolicitacaoExame() == null 
				|| solicitacao.getSolicitacaoExame().getConvenioSaudePlano() == null 
				|| solicitacao.getSolicitacaoExame().getConvenioSaudePlano().getDescricao() == null 
				? "" : solicitacao.getSolicitacaoExame().getConvenioSaudePlano().getDescricao();

		return convenio;
	}

	private String verificarUnidadeSolicitacao(AacConsultas consulta) {
		return consulta == null 
				||consulta.getAtendimento() == null 
				|| consulta.getAtendimento().getUnidadeFuncional() == null 
				? "" : consulta.getAtendimento().getUnidadeFuncional().getDescricao();
	}

	private AacConsultas recuperarConsulta(	List<PesquisaExamesPacientesVO> listaPacientes) {
		AacConsultas consulta = new AacConsultas();
		for(PesquisaExamesPacientesVO pesquisaExame : listaPacientes) {
			if(pesquisaExame.getConsulta() != null) {
				consulta = ambulatorioFacade.obterConsulta(pesquisaExame.getProntuario());
			} 
		}
		return consulta;
	}

	private void recuperarDataHorarioEntrega(AelProtocoloEntregaExames protocolo,
			ProtocoloEntregaExamesVO protocoloEntregaExamesVO) {
		if(protocolo.getCriadoEm() != null) {
			String data = new SimpleDateFormat(DATA).format(protocolo.getCriadoEm());
			protocoloEntregaExamesVO.setDataEntrega(data);
		}
		if(protocolo.getCriadoEm() != null) {
			String horario = new SimpleDateFormat(HORARIO).format(protocolo.getCriadoEm());
			protocoloEntregaExamesVO.setHorarioEntrega(horario);
		}
	}

	private String verificarSituacao(AelItemSolicitacaoExames itemSolicitacaoExame) {
		return itemSolicitacaoExame.getExame() == null 
				|| itemSolicitacaoExame.getExame() .getIndSituacao() == null 
				|| itemSolicitacaoExame.getExame().getIndSituacao().getDescricao() == null 
				? "" : itemSolicitacaoExame.getExame().getIndSituacao().getDescricao();
	}

	private List<AelItemSolicitacaoExames> recuperarItemSolicitacaoExames(Map<Integer, Vector<Short>> listaItens, List<AelItemSolicitacaoExames> itemSolicitacaoExames) {
		for(Map.Entry<Integer, Vector<Short>> vetor : listaItens.entrySet()) {
    		for(Short item : vetor.getValue()) {
    			 AelItemSolicitacaoExames itemSolicitacao = new AelItemSolicitacaoExames();
	    			 itemSolicitacao = recuperarListaSolicitacaoIten(vetor.getKey(), item);
	    			 itemSolicitacaoExames.add(itemSolicitacao);
    		}
    	}
		return itemSolicitacaoExames;
	}

    private AipPacientes recuperarPaciente(List<PesquisaExamesPacientesVO> listaPacientes) {
    	AipPacientes paciente = new AipPacientes();
    	if(listaPacientes != null && !listaPacientes.isEmpty()) {
	    	for(PesquisaExamesPacientesVO pesquisaExamesPacientesVO : listaPacientes) {
	    		paciente = pacienteFacade.pesquisarPacientePorProntuario(pesquisaExamesPacientesVO.getProntuario());
	    	}
    	}
		return paciente;
	}
	
	public List<ResultadoPesquisaProtocoloVO> buscarProtocolo(Long protocolo) {
		
		List<ResultadoPesquisaProtocoloVO> listaProtocolo = new ArrayList<ResultadoPesquisaProtocoloVO>();
		
		if (protocolo != null) {
			AelProtocoloEntregaExames buscarProtocolo = aelProtocoloEntregaExamesDAO.buscarProtocolo(protocolo);
				
				ResultadoPesquisaProtocoloVO resultado = new ResultadoPesquisaProtocoloVO();
				resultado.setProtocolo(buscarProtocolo.getSeq());
				validarRetiradoEm(buscarProtocolo, resultado);
				resultado.setRetiradoPor(validarRetiradoPor(buscarProtocolo));
				resultado.setUsuario(validarUsuario(buscarProtocolo));
				
				listaProtocolo.add(resultado);
			
		} 
		return listaProtocolo;
	}

	private void validarRetiradoEm(AelProtocoloEntregaExames buscarProtocolo,
			ResultadoPesquisaProtocoloVO resultado) {
		if(buscarProtocolo.getCriadoEm() != null) {
			String data = new SimpleDateFormat(DATA).format(buscarProtocolo.getCriadoEm());
			resultado.setRetiradoEm(data);
		}
	}
		
	public List<ResultadoPesquisaProtocoloVO> buscarProtocoloPorProntuario(Integer prontuario) {	
		
		List<ResultadoPesquisaProtocoloVO> listaProtocolo = new ArrayList<ResultadoPesquisaProtocoloVO>();

		if (prontuario != null) {
			List<AelProtocoloEntregaExames> buscarProtocoloSolicitacao = aelProtocoloEntregaExamesDAO.buscarProtocoloPorProntuario(prontuario);
			for (AelProtocoloEntregaExames protocoloExame : buscarProtocoloSolicitacao) {
				
				ResultadoPesquisaProtocoloVO resultado = new ResultadoPesquisaProtocoloVO();
				resultado.setProtocolo(protocoloExame.getSeq());
				validarRetiradoEm(protocoloExame, resultado);
				resultado.setRetiradoPor(validarRetiradoPor(protocoloExame));
				resultado.setUsuario(validarUsuario(protocoloExame));
				
				listaProtocolo.add(resultado);
			}
		} 
		return listaProtocolo;
	}
			
	public List<ResultadoPesquisaProtocoloVO> buscarProtocoloPorSolicitacao(Integer solicitacao) {
		
		List<ResultadoPesquisaProtocoloVO> listaProtocolo = new ArrayList<ResultadoPesquisaProtocoloVO>();
			
			if (solicitacao != null) {
				List<AelProtocoloEntregaExames> buscarProtocoloProntuario = aelProtocoloEntregaExamesDAO.buscarProtocoloPorSolicitacao(solicitacao);
				for (AelProtocoloEntregaExames protocoloExame : buscarProtocoloProntuario) {
					
					ResultadoPesquisaProtocoloVO resultado = new ResultadoPesquisaProtocoloVO();
					resultado.setProtocolo(protocoloExame.getSeq());
					validarRetiradoEm(protocoloExame, resultado);
					resultado.setRetiradoPor(validarRetiradoPor(protocoloExame));
					resultado.setUsuario(validarUsuario(protocoloExame));
					
					listaProtocolo.add(resultado);
				}
			}
		
		return listaProtocolo;
	}

	private String validarUsuario(AelProtocoloEntregaExames protocoloExame) {
		return protocoloExame == null || protocoloExame.getServidor() == null 
				|| protocoloExame.getServidor().getUsuario() == null 
				? "" : protocoloExame.getServidor().getUsuario();
	}

	private String validarRetiradoPor(AelProtocoloEntregaExames protocoloExame) {
		return protocoloExame == null || protocoloExame.getNomeResponsavelRetirada() == null 
				? "" : protocoloExame.getNomeResponsavelRetirada();
	}
	
	public List<ItensProtocoloVO> buscarItensProtocolo(Long protocolo) {
		
		List<ItensProtocoloVO> listaItensProtocolo = new ArrayList<ItensProtocoloVO>();
		
		if (protocolo != null) {
			List<AelItemEntregaExames> itensProtocolo = aelProtocoloEntregaExamesDAO.buscarItensProtocolo(protocolo);
			for (AelItemEntregaExames itemEntregaExames : itensProtocolo) {
				
				ItensProtocoloVO itemProtocolo = new ItensProtocoloVO();
				itemProtocolo.setSolicitacao(validarSolicitacao(itemEntregaExames));
				itemProtocolo.setExame(validarExame(itemEntregaExames));
				
				listaItensProtocolo.add(itemProtocolo);
			}
		}
		
		return listaItensProtocolo;
	}

	private String validarExame(AelItemEntregaExames itemEntregaExames) {
		return verificarDescricaoExameNovoProtocolo(itemEntregaExames);
	}

	private String validarSolicitacao(AelItemEntregaExames itemEntregaExames) {
		return verificarSolicitacaoNovoProtocolo(itemEntregaExames);
	}
    
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorNumProtocolo(Long seq_protocolo) {
		return aelProtocoloEntregaExamesDAO.buscarAipPacientesPorNumProtocolo(seq_protocolo);
	}

	private AelItemSolicitacaoExames recuperarListaSolicitacaoIten(Integer key, Short item) {

        AelItemSolicitacaoExames itemSolicitacao = new AelItemSolicitacaoExames();
        itemSolicitacao = getPesquisaExamesFacade().obterDadoItensSolicitacaoPorSoeSeq(key , item);
        return itemSolicitacao;
    }
	
	private List<AelItemSolicitacaoExames> recuperarListaSolicitacaoItens(Integer key, Short item) {

        List<AelItemSolicitacaoExames> itemSolicitacaos = new ArrayList<AelItemSolicitacaoExames>();
        itemSolicitacaos = getPesquisaExamesFacade().obterDadosItensSolicitacaoPorSoeSeq(key , item);
        return itemSolicitacaos;
    }

    protected AelProtocoloEntregaExamesDAO getAelProtocoloEntregaExamesDAO() {
        return aelProtocoloEntregaExamesDAO;
    }

    protected AelItemEntregaExamesDAO getAelItemEntregaExamesDAO() {
        return aelItemEntregaExamesDAO;
    }

    private IPesquisaExamesFacade getPesquisaExamesFacade() {
        return this.pesquisaExamesFacade;
    }

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}
    
    

}
