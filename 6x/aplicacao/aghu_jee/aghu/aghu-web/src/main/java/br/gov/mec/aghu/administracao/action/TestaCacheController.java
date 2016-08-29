package br.gov.mec.aghu.administracao.action;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class TestaCacheController extends ActionController {
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);	
	}

	@EJB
	protected IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;
	
	@EJB
	protected IPacienteFacade pacienteFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	private static final Log LOG = LogFactory.getLog(TestaCacheController.class);

	private static final long serialVersionUID = -3767585182137474755L;

	private String nomeParametro;
	private Integer codigoPaciente;
	private String nomePaciente;
	private String vlrParametro;
	private String matriculaVinculo;
	
	private String arquivo = "server.log";

	private String logContent;
	
	private List<String> resultadosQueryAf = new ArrayList<String>();
	private List<String> resultadosCacheParametros = new ArrayList<String>();
	private List<String> resultadosCacheEntidades = new ArrayList<String>();
	private List<String> resultadosCacheServidorLogado = new ArrayList<String>();
	
	private static final BigDecimal MILLION = new BigDecimal("1000000");
	
	// tamanho padrão: 250 kb
	private int tamanhoArquivo = 250;

	private static final File SERVER_LOG_DIR = new File(
			System.getProperty("jboss.server.log.dir"));

	public void executarTesteCacheServidorLogado() {
		long startTime = System.nanoTime();
		try {
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			
			
			if (servidorLogado != null) {
				this.matriculaVinculo = servidorLogado.getMatriculaVinculo();
			} else {
				this.matriculaVinculo = "Servidor não encontrado";
			}
			resultadosCacheServidorLogado.add("Matrícula: "+ this.matriculaVinculo+"    Tempo: "+convertMili(duration));
			this.carregarLog();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public String convertMili(long value) {
        StringBuilder result = new StringBuilder();
        BigDecimal bValue = new BigDecimal(value);
        bValue = bValue.divide(MILLION, 4, BigDecimal.ROUND_HALF_EVEN);
        result.append(bValue).append(" ms");
        return result.toString();
    }
	
	public void executarTesteCacheQueryAf() {
		long startTime = System.nanoTime();
		autFornecimentoFacade.pesquisarListaAfsAssinar(1, 5000, null, true, null);
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		resultadosQueryAf.add("Tempo: "+convertMili(duration));
		this.carregarLog();
	}	
	
	public void executarTesteCacheParametros() {
		try {
			long startTime = System.nanoTime();
			AghParametros param =  parametroFacade.buscarAghParametro(AghuParametrosEnum.valueOf(this.nomeParametro));
			long endTime = System.nanoTime();
			long duration = endTime - startTime;
			resultadosCacheParametros.add("Parâmetro: "+this.nomeParametro+"     Tempo: "+convertMili(duration));
			if (param != null) {
				this.vlrParametro = param.getVlrTexto();
			} else {
				this.vlrParametro = "Parâmetro Não Encontrado";
			}
			this.carregarLog();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (IllegalArgumentException e) {
			this.vlrParametro = "Parâmetro Não Existe no Java";
		}
	}
	
	public void executarTesteCacheEntidades() {
		long startTime = System.nanoTime();
		AipPacientes pac = this.pacienteFacade.obterAipPacientesPorChavePrimaria(this.codigoPaciente); 
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		resultadosCacheEntidades.add("Codigo = "+this.codigoPaciente+ "     Tempo = "+convertMili(duration));
		
		if (pac != null) {
			this.setNomePaciente(pac.getNome());
		} else {
			this.setNomePaciente("Paciente não Encontrado");
		}
		this.carregarLog();
	}
	
	public void carregarLog() {
		try {
			if (logContent == null) {
				this.tamanhoArquivo = 250;
			}
			File file = new File(SERVER_LOG_DIR, this.arquivo);

			long auxTamanho = file.length() - (tamanhoArquivo * 1024);

			if (auxTamanho > 0) {
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				raf.seek(auxTamanho);

				StringBuffer sb = new StringBuffer();

				String line = null;
				while ((line = raf.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
				
				raf.close();
				
				this.logContent = sb.toString();
			} else {
				this.logContent = FileUtils.readFileToString(file);
			}
		} catch (IOException e) {
			LOG.error("Erro ao realizar a leitura do arquivo de log "
					+ this.arquivo, e);
		}
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public int getTamanhoArquivo() {
		return tamanhoArquivo;
	}

	public void setTamanhoArquivo(int tamanhoArquivo) {
		this.tamanhoArquivo = tamanhoArquivo;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}

	public String getNomeParametro() {
		return nomeParametro;
	}

	public void setNomeParametro(String nomeParametro) {
		this.nomeParametro = nomeParametro;
	}

	public List<String> getResultadosCacheParametros() {
		return resultadosCacheParametros;
	}

	public void setResultadosCacheParametros(
			List<String> resultadosCacheParametros) {
		this.resultadosCacheParametros = resultadosCacheParametros;
	}

	public List<String> getResultadosCacheEntidades() {
		return resultadosCacheEntidades;
	}

	public void setResultadosCacheEntidades(List<String> resultadosCacheEntidades) {
		this.resultadosCacheEntidades = resultadosCacheEntidades;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getVlrParametro() {
		return vlrParametro;
	}

	public void setVlrParametro(String vlrParametro) {
		this.vlrParametro = vlrParametro;
	}

	public List<String> getResultadosCacheServidorLogado() {
		return resultadosCacheServidorLogado;
	}

	public void setResultadosCacheServidorLogado(
			List<String> resultadosCacheServidorLogado) {
		this.resultadosCacheServidorLogado = resultadosCacheServidorLogado;
	}

	public String getMatriculaVinculo() {
		return matriculaVinculo;
	}

	public void setMatriculaVinculo(String matriculaVinculo) {
		this.matriculaVinculo = matriculaVinculo;
	}

	public List<String> getResultadosQueryAf() {
		return resultadosQueryAf;
	}

	public void setResultadosQueryAf(List<String> resultadosQueryAf) {
		this.resultadosQueryAf = resultadosQueryAf;
	}

}